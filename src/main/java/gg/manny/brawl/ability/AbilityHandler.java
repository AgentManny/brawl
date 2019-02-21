package gg.manny.brawl.ability;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.type.*;
import gg.manny.pivot.Pivot;
import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.handler.PacketHandler;
import gg.manny.spigot.handler.SimpleMovementHandler;
import lombok.Getter;
import org.bukkit.event.Listener;

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
                new Stomper(plugin),
                new SilverfishSwarm(plugin),
                new Charger(plugin),
                new WaterGun(plugin),
                new HealthBooster(),
                new Fisherman()
        );
        this.load();

    }

    private void load() {
        File file = getFile();

        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element != null && element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (Object object : array) {
                    JsonObject jsonObject = (JsonObject) object;
                    Ability ability = Preconditions.checkNotNull(this.getAbilityByName(jsonObject.get("name").getAsString()));
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

            Pivot.GSON.toJson(array, writer);

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

            if (ability instanceof SimpleMovementHandler) {
                GenericSpigot.INSTANCE.addMovementHandler((SimpleMovementHandler) ability);
            }

            if (ability instanceof PacketHandler) {
                GenericSpigot.INSTANCE.addPacketHandler((PacketHandler) ability);
            }
        }
    }

    public Ability getAbilityByName(String name) {
        return this.abilities.get(name);
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
