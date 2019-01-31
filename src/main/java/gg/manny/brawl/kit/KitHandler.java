package gg.manny.brawl.kit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.command.BukkitCommand;
import gg.manny.pivot.Pivot;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KitHandler {

    private final Brawl plugin;

    @Getter
    private List<Kit> kits = new ArrayList<>();

    public KitHandler(Brawl plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load() {
        File file = getFile();
        try (FileReader reader = new FileReader(file)) {

            JsonObject jsonObject = Pivot.GSON.fromJson(reader, JsonObject.class);
            for (JsonElement element : jsonObject.get("kits").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                this.registerKit(new Kit(object));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.save();
    }

    public void save() {
        File file = getFile();

        try (FileWriter writer = new FileWriter(file)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("kits", this.toJson());

            Pivot.GSON.toJson(jsonObject, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (Kit kit : this.kits) {
            jsonArray.add(kit.toJson());
        }
        return jsonArray;
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "kits.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void registerKit(Kit kit) {
        this.kits.add(kit);
        new BukkitCommand(plugin, kit.getName());
    }

    public Kit getDefaultKit() {
        return this.getKit("PVP");
    }

    public Kit getKit(String name) {
        return this.kits.stream().
                filter(kit -> kit.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
}