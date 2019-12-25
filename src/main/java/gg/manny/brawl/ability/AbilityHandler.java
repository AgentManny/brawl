package gg.manny.brawl.ability;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.type.*;
import gg.manny.brawl.ability.type.chemist.Chemist;
import gg.manny.brawl.ability.type.chemist.SmokeBomb;
import gg.manny.pivot.Pivot;
import gg.manny.server.MineServer;
import gg.manny.server.handler.PacketHandler;
import gg.manny.server.handler.SimpleMovementHandler;
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
                new Chemist(),
                new SmokeBomb(),

                new Fireball(),
                new Toss(),

                new FluffyHandcuffs(),

                new Stomper(plugin),
                new SilverfishSwarm(plugin),
                new Charger(plugin),
                new WaterGun(plugin),
                new HealthBooster(),
                new Fisherman(),
                new FlameThrower(),
                new IceSpikes(),
                new Vortex(plugin),
                new Detonator(plugin),
                new Vampire(plugin),
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


                new NinjaStars()
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
                MineServer.getInstance().addMovementHandler((SimpleMovementHandler) ability);
            }

            if (ability instanceof PacketHandler) {
                MineServer.getInstance().addPacketHandler((PacketHandler) ability);
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
