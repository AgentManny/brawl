package gg.manny.brawl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.ability.AbilityHandler;
import gg.manny.brawl.ability.command.AbilityCommand;
import gg.manny.brawl.ability.command.adapter.AbilityTypeAdapter;
import gg.manny.brawl.chat.BrawlChatFormat;
import gg.manny.brawl.command.*;
import gg.manny.brawl.duelarena.DuelArenaHandler;
import gg.manny.brawl.duelarena.arena.Arena;
import gg.manny.brawl.duelarena.command.ArenaCommand;
import gg.manny.brawl.duelarena.command.DuelCommand;
import gg.manny.brawl.duelarena.command.ViewMatchInvCommand;
import gg.manny.brawl.duelarena.command.adapter.ArenaCommandAdapter;
import gg.manny.brawl.event.EventHandler;
import gg.manny.brawl.game.GameHandler;
import gg.manny.brawl.item.ItemHandler;
import gg.manny.brawl.killstreak.KillstreakHandler;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.KitHandler;
import gg.manny.brawl.kit.command.KitCommand;
import gg.manny.brawl.kit.command.adapter.KitTypeAdapter;
import gg.manny.brawl.leaderboard.Leaderboard;
import gg.manny.brawl.listener.*;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerDataHandler;
import gg.manny.brawl.player.adapter.PlayerDataTypeAdapter;
import gg.manny.brawl.player.cps.ClickTracker;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.player.simple.adapter.SimpleOfflinePlayerAdapter;
import gg.manny.brawl.rail.Rail;
import gg.manny.brawl.region.RegionHandler;
import gg.manny.brawl.region.command.RegionCommands;
import gg.manny.brawl.scoreboard.ScoreboardAdapter;
import gg.manny.brawl.spectator.SpectatorManager;
import gg.manny.brawl.task.SoupTask;
import gg.manny.brawl.team.TeamHandler;
import gg.manny.brawl.util.EntityHider;
import gg.manny.brawl.warp.WarpManager;
import gg.manny.construct.Construct;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.nametag.Nametag;
import gg.manny.pivot.nametag.NametagHandler;
import gg.manny.pivot.nametag.NametagProvider;
import gg.manny.pivot.serialization.*;
import gg.manny.pivot.util.file.type.BasicConfigurationFile;
import gg.manny.quantum.Quantum;
import gg.manny.server.MineServer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class Brawl extends JavaPlugin {

    @Getter
    public static Brawl instance;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())

            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static Random RANDOM = new Random();

    private MongoDatabase mongoDatabase;

    private PlayerDataHandler playerDataHandler;

    private AbilityHandler abilityHandler;
    private KillstreakHandler killstreakHandler;

    private KitHandler kitHandler;

    private DuelArenaHandler matchHandler;

    private GameHandler gameHandler;
    private EventHandler eventHandler;

    private TeamHandler teamHandler;

    private WarpManager warpManager;

    private SpectatorManager spectatorManager;

    private Leaderboard leaderboard;

    private RegionHandler regionHandler;

    private ItemHandler itemHandler;

    private BasicConfigurationFile mainConfig;

    private EntityHider entityHider;

    private Construct construct;

    private Map<String, Location> locationMap = new HashMap<>();

    private BasicConfigurationFile databaseFile;

    private boolean loaded = false;

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new BasicConfigurationFile(this, "config");
        this.databaseFile = new BasicConfigurationFile(this, "database");

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.loadDatabase();
        this.loadLocations();
        this.registerHandlers();
        this.registerCommands();



        new Rail();

        new SoupTask(this).runTaskTimer(this, 20L, 20L);

        this.getServer().getWorlds().forEach(world -> world.getEntitiesByClass(Item.class).forEach(Item::remove));

        MovementListener movementListener = new MovementListener(this);
        MineServer.getInstance().addMovementHandler(movementListener);

        Arrays.asList(new ClickTracker(this), new AbilityListener(this), new ToolInteractListener(), new ProtectListener(this), new ArenaListener(this), new PlayerListener(this), new DamageListener(this), new SoupListener(this), new TeamListener(this), movementListener)
                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
        loaded = true;
    }

    @Override
    public void onDisable() {
        this.playerDataHandler.close();
        this.regionHandler.close();
        this.teamHandler.save(true);
        matchHandler.onDisable();
        this.kitHandler.save();
        this.abilityHandler.save();
        this.gameHandler.save();
        eventHandler.save();

        try {
            SimpleOfflinePlayer.save(this);
            mainConfig.getConfiguration().save(mainConfig.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        Quantum quantum = Pivot.getInstance().getQuantum();

        quantum.registerParameterType(Arena.class, new ArenaCommandAdapter());
        quantum.registerParameterType(Ability.class, new AbilityTypeAdapter(this));
        quantum.registerParameterType(Kit.class, new KitTypeAdapter(this));
        quantum.registerParameterType(SimpleOfflinePlayer.class, new SimpleOfflinePlayerAdapter());
        quantum.registerParameterType(PlayerData.class, new PlayerDataTypeAdapter(this));

        Arrays.asList(

                new KillstreakCommand(this),

                new ArenaCommand(this),
                new ViewMatchInvCommand(),
                new DuelCommand(this),

                new StatsCommand(),
                new LeaderboardCommand(),
                new AbilityCommand(),
                new BuildCommand(this),
                new BrawlCommand(this),
                new SpawnCommand(this),
                new KitCommand(this),
                new RegionCommands(this),
                new ClearkitCommand(this)
        ).forEach(quantum::registerCommand);
    }

    private void registerHandlers() {
        this.abilityHandler = new AbilityHandler(this);
        this.killstreakHandler = new KillstreakHandler(this);

        this.kitHandler = new KitHandler(this);

        spectatorManager = new SpectatorManager(this);
        this.matchHandler = new DuelArenaHandler();


        SimpleOfflinePlayer.load(this);
        this.playerDataHandler = new PlayerDataHandler(this);

        this.eventHandler = new EventHandler();
        this.gameHandler = new GameHandler(this);
        this.teamHandler = new TeamHandler();
        this.warpManager = new WarpManager();

        this.leaderboard = new Leaderboard(this);

        this.regionHandler = new RegionHandler(this);

        this.itemHandler = new ItemHandler(this);

        Construct construct = new Construct(this, new ScoreboardAdapter(this));
        construct.setUpdateInterval(150L);
        construct.setShowHealth(true);



        Pivot.getInstance().getChatHandler().setChatFormat(new BrawlChatFormat(Pivot.getInstance()));

        NametagHandler nh = Pivot.getInstance().getNametagHandler();
        nh.registerProvider(this.registerNametag());

    }

    private NametagProvider registerNametag() {
        return new NametagProvider("Brawl", 99) {
            @Override
            public Nametag fetchNametag(Player toRefresh, Player refreshFor) {
                return createNametag(Pivot.getInstance().getProfileHandler().getProfile(toRefresh).getRank().getColor(), "");
            }
        };
    }

    public static void broadcastOps(String message) {
        for (Player onlinePlayer : getInstance().getServer().getOnlinePlayers()) {
            if (onlinePlayer.isOp()) {
                onlinePlayer.sendMessage(message);
            }
        }
    }

    private void loadDatabase() {
        String database = this.databaseFile.getString("mongo.database");
        String[] address = this.databaseFile.getString("mongo.host").split(":");
        String host = address[0];
        int port = Integer.parseInt(address[1]);
        String password = this.databaseFile.getString("mongo.password");
        String username = this.databaseFile.getString("mongo.username");

        if (password.isEmpty()) {
            mongoDatabase = new MongoClient(host, port).getDatabase(database);
        } else {
            mongoDatabase = new MongoClient(
                    new ServerAddress(host, port),
                    MongoCredential.createCredential(username, "admin", password.toCharArray()),
                    MongoClientOptions.builder().build()
            ).getDatabase(database);
        }
    }

    private void loadLocations() {
        ConfigurationSection configurationSection = this.mainConfig.getConfiguration().getConfigurationSection("LOCATIONS");
        if (configurationSection == null) return;
        for(String key : configurationSection.getKeys(false)) {
            World world = getServer().getWorld(configurationSection.getString(key + ".world"));
            double x = configurationSection.getDouble(key + ".x");
            double y = configurationSection.getDouble(key + ".y");
            double z = configurationSection.getDouble(key + ".z");
            float yaw = configurationSection.getFloat(key + ".yaw");
            float pitch = configurationSection.getFloat(key + ".pitch");
            this.locationMap.put(key, new Location(world, x, y, z, yaw, pitch));
        }
    }

    public Location getLocationByName(String locationName) {
        if(this.locationMap.containsKey(locationName)) {
            return this.locationMap.get(locationName);
        }

        return this.getServer().getWorld("world").getSpawnLocation();
    }

    public void setLocationByName(String locationName, Location location) {
        this.locationMap.put(locationName, location);

        FileConfiguration config = this.mainConfig.getConfiguration();
        String prefix = "LOCATIONS." + locationName.toUpperCase() + ".";
        config.set(prefix + "world", location.getWorld().getName());
        config.set(prefix + "x", location.getX());
        config.set(prefix + "y", location.getY());
        config.set(prefix + "z", location.getZ());
        config.set(prefix + "yaw", location.getYaw());
        config.set(prefix + "pitch", location.getPitch());
        try {
            this.mainConfig.getConfiguration().save(this.mainConfig.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
