package rip.thecraft.brawl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityHandler;
import rip.thecraft.brawl.ability.command.AbilityCommand;
import rip.thecraft.brawl.ability.command.adapter.AbilityTypeAdapter;
import rip.thecraft.brawl.command.*;
import rip.thecraft.brawl.command.manage.ExpModifyCommand;
import rip.thecraft.brawl.command.manage.VisualCommand;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.arena.Arena;
import rip.thecraft.brawl.duelarena.command.ArenaCommand;
import rip.thecraft.brawl.duelarena.command.DuelCommand;
import rip.thecraft.brawl.duelarena.command.ViewMatchInvCommand;
import rip.thecraft.brawl.duelarena.command.adapter.ArenaCommandAdapter;
import rip.thecraft.brawl.event.EventHandler;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.item.ItemHandler;
import rip.thecraft.brawl.killstreak.KillstreakHandler;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.brawl.kit.command.KitCommand;
import rip.thecraft.brawl.kit.command.adapter.KitTypeAdapter;
import rip.thecraft.brawl.leaderboard.Leaderboard;
import rip.thecraft.brawl.leaderboard.command.LeaderboardCommand;
import rip.thecraft.brawl.listener.*;
import rip.thecraft.brawl.market.MarketHandler;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerDataHandler;
import rip.thecraft.brawl.player.adapter.PlayerDataTypeAdapter;
import rip.thecraft.brawl.player.cps.ClickTracker;
import rip.thecraft.brawl.region.RegionHandler;
import rip.thecraft.brawl.region.command.RegionCommands;
import rip.thecraft.brawl.scoreboard.BrawlNametagAdapter;
import rip.thecraft.brawl.scoreboard.BrawlScoreboardAdapter;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.task.SoupTask;
import rip.thecraft.brawl.team.TeamHandler;
import rip.thecraft.brawl.upgrade.UpgradeManager;
import rip.thecraft.brawl.util.EntityHider;
import rip.thecraft.brawl.visual.VisualManager;
import rip.thecraft.brawl.warp.WarpManager;
import rip.thecraft.server.CraftServer;
import rip.thecraft.spartan.command.MCommandHandler;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.scoreboard.MScoreboardHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class Brawl extends JavaPlugin {

    @Getter
    public static Brawl instance;

    public static Random RANDOM = new Random();

    private MongoDatabase mongoDatabase;

    private PlayerDataHandler playerDataHandler;

    private AbilityHandler abilityHandler;

    // Upgrade section
    private KillstreakHandler killstreakHandler;
    private UpgradeManager upgradeManager;

    private MarketHandler marketHandler;
    private KitHandler kitHandler;

    private DuelArenaHandler matchHandler;

    private GameHandler gameHandler;
    private EventHandler eventHandler;

    private TeamHandler teamHandler;

    private WarpManager warpManager;

    private SpectatorManager spectatorManager;

    private Leaderboard leaderboard;
    private VisualManager visualManager;

    private RegionHandler regionHandler;

    private ItemHandler itemHandler;

    private EntityHider entityHider;

    private Map<String, Location> locationMap = new HashMap<>();

    private boolean loaded = false;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        loadDatabase();

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.loadLocations();
        this.registerHandlers();
        this.registerCommands();

        new SoupTask(this).runTaskTimer(this, 20L, 20L);

        this.getServer().getWorlds().forEach(world -> {
            world.getEntitiesByClass(Item.class).forEach(Item::remove);
            world.getEntitiesByClass(TNTPrimed.class).forEach(TNTPrimed::remove);
            world.getEntitiesByClass(Wolf.class).forEach(Wolf::remove);
        });

        MovementListener movementListener = new MovementListener(this);
        CraftServer.getInstance().addMovementHandler(movementListener);

        Arrays.asList(new ClickTracker(this), new AbilityListener(this), new ToolInteractListener(), new ProtectListener(this), new ArenaListener(this), new PlayerListener(this), new DamageListener(this), new SoupListener(this), new TeamListener(this), movementListener)
                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
        loaded = true;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = playerDataHandler.getPlayerData(player);
            playerData.save();
        }

        getServer().getScheduler().getPendingTasks().forEach(BukkitTask::cancel);


        Game game = gameHandler.getActiveGame();
        if (game != null) {
            game.end();
        }

        this.playerDataHandler.close();
        this.regionHandler.close();
        this.teamHandler.save(true);
        matchHandler.onDisable();
        this.kitHandler.save();
        this.abilityHandler.save();
        this.gameHandler.save();
        eventHandler.save();
    }

    private void registerCommands() {
        MCommandHandler.registerParameterType(Arena.class, new ArenaCommandAdapter());
        MCommandHandler.registerParameterType(Ability.class, new AbilityTypeAdapter(this));
        MCommandHandler.registerParameterType(Kit.class, new KitTypeAdapter(this));
        MCommandHandler.registerParameterType(PlayerData.class, new PlayerDataTypeAdapter(this));

        Arrays.asList(
                new HelpCommand(),
                new SetRefillCommand(),

                // Manage commands
                new ExpModifyCommand(),
                new VisualCommand(this),

                new KillstreakCommand(this),

                new ArenaCommand(this),
                new ViewMatchInvCommand(),
                new DuelCommand(this),

                new StatsCommand(),
                new LeaderboardCommand(),
                new AbilityCommand(),
                new SpawnCommand(this),
                new KitCommand(this),
                new RegionCommands(this),
                new ClearkitCommand(this)
        ).forEach(MCommandHandler::registerCommand);
    }

    private void registerHandlers() {
        this.playerDataHandler = new PlayerDataHandler(this);
        this.kitHandler = new KitHandler(this);

        spectatorManager = new SpectatorManager(this);
        this.matchHandler = new DuelArenaHandler();

        this.abilityHandler = new AbilityHandler(this);

        this.killstreakHandler = new KillstreakHandler(this);
        this.upgradeManager = new UpgradeManager(this);

        this.marketHandler = new MarketHandler();
        this.eventHandler = new EventHandler();
        this.gameHandler = new GameHandler(this);
        this.teamHandler = new TeamHandler();
        this.warpManager = new WarpManager();

        this.leaderboard = new Leaderboard(this);
        this.visualManager = new VisualManager(this);

        this.regionHandler = new RegionHandler(this);

        this.itemHandler = new ItemHandler(this);

        MScoreboardHandler.setAdapter(new BrawlScoreboardAdapter(this));
        NametagHandler.registerProvider(new BrawlNametagAdapter(this));
    }
    public static void broadcastOps(String message) {
        for (Player onlinePlayer : getInstance().getServer().getOnlinePlayers()) {
            if (onlinePlayer.isOp()) {
                onlinePlayer.sendMessage(message);
            }
        }
    }

    private void loadDatabase() {
        FileConfiguration config = getConfig();
        String database = config.getString("mongo.database");
        String[] address = config.getString("mongo.host").split(":");
        String host = address[0];
        int port = Integer.parseInt(address[1]);
        String password = config.getString("mongo.password");
        String username = config.getString("mongo.username");

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
        ConfigurationSection configurationSection = getConfig().getConfigurationSection("LOCATIONS");
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

        FileConfiguration config = getConfig();
        String prefix = "LOCATIONS." + locationName.toUpperCase() + ".";
        config.set(prefix + "world", location.getWorld().getName());
        config.set(prefix + "x", location.getX());
        config.set(prefix + "y", location.getY());
        config.set(prefix + "z", location.getZ());
        config.set(prefix + "yaw", location.getYaw());
        config.set(prefix + "pitch", location.getPitch());
        saveConfig();
    }
}
