package gg.manny.brawl;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.ability.AbilityHandler;
import gg.manny.brawl.ability.command.AbilityCommand;
import gg.manny.brawl.ability.command.adapter.AbilityTypeAdapter;
import gg.manny.brawl.command.BrawlCommand;
import gg.manny.brawl.command.SpawnCommand;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.KitHandler;
import gg.manny.brawl.kit.command.KitCommand;
import gg.manny.brawl.kit.command.adapter.KitTypeAdapter;
import gg.manny.brawl.listener.DamageListener;
import gg.manny.brawl.listener.MovementListener;
import gg.manny.brawl.listener.PlayerListener;
import gg.manny.brawl.listener.SoupListener;
import gg.manny.brawl.nametag.KitNametagProvider;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerDataHandler;
import gg.manny.brawl.player.adapter.PlayerDataTypeAdapter;
import gg.manny.brawl.region.RegionHandler;
import gg.manny.brawl.region.command.RegionCommand;
import gg.manny.brawl.scoreboard.ScoreboardAdapter;
import gg.manny.brawl.task.SoupTask;
import gg.manny.brawl.util.item.ItemHandler;
import gg.manny.construct.Construct;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.EntityHider;
import gg.manny.pivot.util.file.type.BasicConfigurationFile;
import gg.manny.pivot.util.serialization.LocationSerializer;
import gg.manny.quantum.Quantum;
import gg.manny.spigot.GenericSpigot;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

    private RegionHandler regionHandler;

    private ItemHandler itemHandler;

    private WorldEditPlugin worldEdit;

    private BasicConfigurationFile mainConfig;

    private EntityHider entityHider;

    private Quantum quantum;

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
    }

    private void registerCommands() {
        this.quantum = new Quantum(this);

        this.quantum.registerParameterType(Ability.class, new AbilityTypeAdapter(this));
        this.quantum.registerParameterType(Kit.class, new KitTypeAdapter(this));
        this.quantum.registerParameterType(PlayerData.class, new PlayerDataTypeAdapter(this));

        Arrays.asList(
                new AbilityCommand(),
                new BrawlCommand(this),
                new SpawnCommand(this),
                new KitCommand(this),
                new RegionCommand(this)
        ).forEach(quantum::registerCommand);
    }

    private void registerHandlers() {
        this.playerDataHandler = new PlayerDataHandler(this);
        this.kitHandler = new KitHandler(this);

        Plugin worldEditPlugin = getServer().getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = worldEditPlugin instanceof WorldEditPlugin && worldEditPlugin.isEnabled() ? (WorldEditPlugin) worldEditPlugin : null;

        this.regionHandler = new RegionHandler(this);

        this.abilityHandler = new AbilityHandler(this);

        this.itemHandler = new ItemHandler(this);

        this.construct = new Construct(this, new ScoreboardAdapter(this));
        Pivot.getPlugin().getNametagHandler().registerProvider(new KitNametagProvider(this));
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
            Location location = LocationSerializer.deserialize(BasicDBObject.parse(configurationSection.getString(key)));
            this.locationMap.put(key, location);
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

        this.mainConfig.getConfiguration().set("LOCATIONS." + locationName.toUpperCase(), LocationSerializer.serialize(location).toJson());
        try {
            this.mainConfig.getConfiguration().save(this.mainConfig.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
