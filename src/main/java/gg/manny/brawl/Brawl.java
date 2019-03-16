package gg.manny.brawl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.ability.AbilityHandler;
import gg.manny.brawl.ability.command.AbilityCommand;
import gg.manny.brawl.ability.command.adapter.AbilityTypeAdapter;
import gg.manny.brawl.command.BrawlCommand;
import gg.manny.brawl.command.BuildCommand;
import gg.manny.brawl.command.SpawnCommand;
import gg.manny.brawl.game.GameHandler;
import gg.manny.brawl.item.ItemHandler;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.KitHandler;
import gg.manny.brawl.kit.command.KitCommand;
import gg.manny.brawl.kit.command.adapter.KitTypeAdapter;
import gg.manny.brawl.listener.DamageListener;
import gg.manny.brawl.listener.MovementListener;
import gg.manny.brawl.listener.PlayerListener;
import gg.manny.brawl.listener.SoupListener;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerDataHandler;
import gg.manny.brawl.player.adapter.PlayerDataTypeAdapter;
import gg.manny.brawl.region.RegionHandler;
import gg.manny.brawl.region.command.RegionCommand;
import gg.manny.brawl.scoreboard.ScoreboardAdapter;
import gg.manny.brawl.task.SoupTask;
import gg.manny.brawl.team.Team;
import gg.manny.brawl.team.TeamHandler;
import gg.manny.brawl.team.command.adapter.TeamTypeAdapter;
import gg.manny.construct.Construct;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.nametag.Nametag;
import gg.manny.pivot.nametag.NametagProvider;
import gg.manny.pivot.util.EntityHider;
import gg.manny.pivot.util.file.type.BasicConfigurationFile;
import gg.manny.quantum.Quantum;
import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
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

    private KitHandler kitHandler;

    private AbilityHandler abilityHandler;

    private GameHandler gameHandler;

    private TeamHandler teamHandler;

    private RegionHandler regionHandler;

    private ItemHandler itemHandler;

    private WorldEditPlugin worldEdit;

    private BasicConfigurationFile mainConfig;

    private EntityHider entityHider;

    private Construct construct;

    private Map<String, Location> locationMap = new HashMap<>();

    private boolean loaded = false;

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new BasicConfigurationFile(this, "config");
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.loadDatabase();
        this.loadLocations();
        this.registerHandlers();
        this.registerCommands();

        new SoupTask(this).runTaskTimer(this, 20L, 20L);

        MovementListener movementListener = new MovementListener(this);
        GenericSpigot.INSTANCE.addMovementHandler(movementListener);

        Arrays.asList(new PlayerListener(this), new DamageListener(this), new SoupListener(this), movementListener)
                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        loaded = true;
    }

    @Override
    public void onDisable() {
        this.playerDataHandler.close();
        this.regionHandler.close();
        this.teamHandler.save();
        this.kitHandler.save();
        this.abilityHandler.save();
        this.gameHandler.save();
    }

    private void registerCommands() {
        Quantum quantum = Pivot.getPlugin().getQuantum();

        quantum.registerParameterType(Ability.class, new AbilityTypeAdapter(this));
        quantum.registerParameterType(Kit.class, new KitTypeAdapter(this));
        quantum.registerParameterType(PlayerData.class, new PlayerDataTypeAdapter(this));
        quantum.registerParameterType(Team.class, new TeamTypeAdapter(this));

        Arrays.asList(
                new AbilityCommand(),
                new BuildCommand(this),
                new BrawlCommand(this),
                new SpawnCommand(this),
                new KitCommand(this),
                new RegionCommand(this)
        ).forEach(quantum::registerCommand);
    }

    private void registerHandlers() {
        this.abilityHandler = new AbilityHandler(this);
        this.kitHandler = new KitHandler(this);
        this.playerDataHandler = new PlayerDataHandler(this);
        this.gameHandler = new GameHandler(this);
      //  this.teamHandler = new TeamHandler(this); //Todo Fix teams :(

        Plugin worldEditPlugin = getServer().getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = worldEditPlugin instanceof WorldEditPlugin && worldEditPlugin.isEnabled() ? (WorldEditPlugin) worldEditPlugin : null;

        this.regionHandler = new RegionHandler(this);

        this.itemHandler = new ItemHandler(this);

        this.construct = new Construct(this, new ScoreboardAdapter(this));
        this.construct.setUpdateInterval(100L);
        Pivot.getPlugin().getNametagHandler().registerProvider(this.registerNametag());
    }

    private NametagProvider registerNametag() {
        return new NametagProvider("Brawl", 50) {
            @Override
            public Nametag fetchNametag(Player toRefresh, Player refreshFor) {
                Scoreboard scoreboard = toRefresh.getScoreboard();
                if (scoreboard != null && scoreboard.getObjective("health") != null) {
                    Objective objective = scoreboard.registerNewObjective("health", "health");
                    objective.setDisplayName(CC.DARK_RED + "\u2764");
                    objective.getScore(refreshFor).setScore((int) toRefresh.getHealth());
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                }

                return createNametag(Pivot.getPlugin().getProfileHandler().getProfile(toRefresh).getRank().getColor(), "");
            }
        };
    }
    private void loadDatabase() {
        if (mainConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            mongoDatabase = new MongoClient(
                    new ServerAddress(mainConfig.getString("MONGO.HOST"), mainConfig.getInteger("MONGO.PORT")),
                    MongoCredential.createCredential(mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"), "admin", mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()),
                    MongoClientOptions.builder().build()
            ).getDatabase(mainConfig.getString("MONGO.DATABASE"));
        } else {
            mongoDatabase = new MongoClient(mainConfig.getString("MONGO.HOST"), mainConfig.getInteger("MONGO.PORT"))
                    .getDatabase(mainConfig.getString("MONGO.DATABASE"));
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
