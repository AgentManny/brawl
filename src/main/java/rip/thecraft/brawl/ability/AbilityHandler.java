package rip.thecraft.brawl.ability;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.abilities.*;
import rip.thecraft.brawl.ability.abilities.classic.Fisherman;
import rip.thecraft.brawl.ability.abilities.classic.Gambler;
import rip.thecraft.brawl.ability.abilities.classic.Stomper;
import rip.thecraft.brawl.ability.abilities.legacy.*;
import rip.thecraft.brawl.ability.abilities.skylands.Archer;
import rip.thecraft.brawl.ability.abilities.skylands.Charger;
import rip.thecraft.brawl.ability.abilities.skylands.SilverfishSwarm;
import rip.thecraft.brawl.ability.abilities.skylands.WaterGun;
import rip.thecraft.brawl.ability.abilities.skylands.chemist.Chemist;
import rip.thecraft.brawl.ability.abilities.skylands.chemist.SmokeBomb;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.MovementHandler;
import rip.thecraft.server.handler.PacketHandler;
import rip.thecraft.spartan.Spartan;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbilityHandler {

    private final Brawl plugin;

    @Getter
    private Map<String, Ability> abilities = new HashMap<>();

    public AbilityHandler(Brawl plugin) {
        this.plugin = plugin;

        this.registerAbilities(
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
                new SilverfishSwarm(plugin),
                new Charger(plugin),
                new WaterGun(plugin),
                new HealthBooster(),
                new Fisherman(),
                new FlameThrower(),
                new IceSpikes(),
                new Vortex(plugin),
                new Detonator(plugin),
                new Vampire(),
                new Rider(plugin),
                new WebShooter(plugin),
                new Dash(),
                new Archer(),
                new Switcher(),
                new Grappler(),
                new Medic(),
                new Rapid(),
                new ShadowShift(),
                new Smite(),


                new Shurikens(),
                new Blaze(),
                new Gambler(),
                new Assassin(),
                new Phantom(),

                new Illusioner()
        );
        this.load();

    }

    public void close() {
        abilities.values().forEach(Ability::cleanup);
    }

    private void load() {
        File file = getFile();

        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element != null && element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (Object object : array) {
                    JsonObject jsonObject = (JsonObject) object;
                    Ability ability = this.getAbilityByName(jsonObject.get("name").getAsString());
                    if (ability == null) continue; // Ability no longer exists

                    ability.fromJson(jsonObject);
                }
            } else {
                plugin.getLogger().severe("Could not load " + file.getName() + " as it isn't an array.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.save();
    }

    public void save() {
        File file = getFile();

        try (FileWriter writer = new FileWriter(file)) {

            JsonArray array = new JsonArray();
            this.abilities.values().forEach(ability -> array.add(ability.toJson()));

            Spartan.GSON.toJson(array, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            this.abilities.put(ability.getName(), ability);

            if (ability instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) ability, plugin);
            }

            if (ability instanceof MovementHandler) {
                CraftServer.getInstance().addMovementHandler((MovementHandler) ability);
            }

            if (ability instanceof PacketHandler) {
                CraftServer.getInstance().addPacketHandler((PacketHandler) ability);
            }
        }
    }

    public Ability getAbilityByName(String name) {
        for (Map.Entry<String, Ability> entry : this.abilities.entrySet()) {
            if (entry.getKey().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return entry.getValue();
            }
        }
        return null;
    }

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
