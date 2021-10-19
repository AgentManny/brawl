package rip.thecraft.brawl.ability;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.abilities.*;
import rip.thecraft.brawl.ability.abilities.classic.Fisherman;
import rip.thecraft.brawl.ability.abilities.classic.Gambler;
import rip.thecraft.brawl.ability.abilities.classic.Stomper;
import rip.thecraft.brawl.ability.abilities.legacy.Grappler;
import rip.thecraft.brawl.ability.abilities.legacy.Vortex;
import rip.thecraft.brawl.ability.abilities.skylands.Archer;
import rip.thecraft.brawl.ability.abilities.skylands.Charger;
import rip.thecraft.brawl.ability.abilities.skylands.SilverfishSwarm;
import rip.thecraft.brawl.ability.abilities.skylands.WaterGun;
import rip.thecraft.brawl.ability.abilities.skylands.chemist.Chemist;
import rip.thecraft.brawl.ability.abilities.skylands.chemist.SmokeBomb;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.task.AbilityTask;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.MovementHandler;
import rip.thecraft.server.handler.PacketHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityHandler {

    private final Brawl plugin;

    @Getter
    private Map<String, Ability> abilities = new HashMap<>();

    /** Returns all ongoing ability tasks */
    private Map<UUID, AbilityTask> activeTasks = new ConcurrentHashMap<>();

    public AbilityHandler(Brawl plugin) {
        this.plugin = plugin;

        plugin.getLogger().info("[Ability Manager] Loading abilities...");
        Arrays.asList(
                new Chemist(),
                new SmokeBomb(),

                new Fireball(),
                new Toss(),

                new SnowGlobe(),
                new Hellhound(),

                // MCPVP
                new Flash(),
                new Kangaroo(),

                new BatBlaster(),

                new TimeLock(),

                new Stomper(),
                new SilverfishSwarm(),
                new Charger(),
                new WaterGun(),
                new HealthBooster(),
                new Fisherman(),

                new Vortex(),
                new Detonator(),
//                new Vampire(),
                new Rider(),
                new WebShooter(),
                new Dash(),
                new Archer(),
                new Switcher(),
                new Grappler(),
                new Medic(),
                // new ShadowShift(),
                new Smite(),

                new Shurikens(),
                new Blaze(),
                new Gambler(),
                new Assassin(),
                new Phantom(),
                new Dragon(),
                new Clown()
        ).forEach(this::registerAbility);
        load();
        plugin.getLogger().info("[Ability Manager] Loaded " + abilities.size() + " abilities.");
    }

    /**
     * Clears any cached data stored in
     * abilities
     */
    public void close() {
        abilities.values().forEach(Ability::cleanup);
    }

    /**
     * Adds an ability to the registry
     * @param ability Ability to add
     */
    private void registerAbility(Ability ability) {
        // Access @AbilityData to fetch information of ability
        Class<? extends Ability> clazz = ability.getClass();
        AbilityData abilityData = clazz.getAnnotation(AbilityData.class);
        if (abilityData == null) {
            plugin.getLogger().warning("[Ability] " + clazz.getSimpleName() + " failed to register as it doesn't have AbilityData annotation");
            return;
        }
        ability.load(abilityData); // Loaded parameters

        // Register listeners
        if (ability instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) ability, plugin);
        }

        if (ability instanceof MovementHandler) {
            CraftServer.getInstance().addMovementHandler((MovementHandler) ability);
        }

        if (ability instanceof PacketHandler) {
            CraftServer.getInstance().addPacketHandler((PacketHandler) ability);
        }
        abilities.put(ability.getName(), ability);
    }

    /**
     * Serializes all abilities properties into
     * a document.
     * @return Serialized abilities
     */
    public Document serialize() {
        Document abilities = new Document();
        for (Ability ability : this.abilities.values()) {
            Document serialize = ability.serialize();
            abilities.put(ability.getName(), serialize);
        }
        return abilities;
    }

    /**
     * Loads ability from disk
     */
    public void load() {
        File file = getFile();
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element != null && element.isJsonObject()) {
                String json = element.toString();
                Document document = Document.parse(json);
                document.forEach((key, value) -> getAbility(key).ifPresent(ability -> {
                    ability.deserialize((Document) value);
                }));
            } else {
                plugin.getLogger().severe("[Ability Manager] Could not load " + file.getName() + " as it isn't a json object.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        save();
    }

    /**
     * Saves abilities to disk
     */
    public void save() {
        Document abilities = serialize();

        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            String json = abilities.toJson(JsonWriterSettings.builder()
                    .indent(true)
                    .outputMode(JsonMode.SHELL)
                    .build());
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an ability from the name
     * @param name Name of ability
     * @return Ability
     */
    public Optional<Ability> getAbility(String name) {
        return getAbilities().values().stream()
                .filter(ability -> ability.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))
                .findAny();
    }

    /**
     * Gets an ability from the name
     * @param name Name of ability
     * @return Ability
     */
    public Ability getAbilityByName(String name) {
        for (Map.Entry<String, Ability> entry : this.abilities.entrySet()) {
            if (entry.getKey().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Gets an ability by the class
     * @param clazz Class of ability
     * @return Ability
     */
    public <T extends Ability> T getAbilityByClass(Class<T> clazz) {
        for (Map.Entry<String, Ability> entry : this.abilities.entrySet()) {
            if (entry.getValue().getClass().equals(clazz)) {
                return (T) entry.getValue();
            }
        }
        return null;
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "abilities.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}