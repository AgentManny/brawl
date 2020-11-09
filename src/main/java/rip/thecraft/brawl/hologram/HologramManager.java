package rip.thecraft.brawl.hologram;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.hologram.hologram.Hologram;
import rip.thecraft.brawl.hologram.hologram.HologramListener;
import rip.thecraft.brawl.hologram.hologram.Holograms;
import rip.thecraft.brawl.hologram.hologram.UpdatingHologram;
import rip.thecraft.spartan.Spartan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HologramManager {

    @Getter
    private Map<Integer, Hologram> holograms = new HashMap<>();

    public HologramManager() {
        File file = new File(Brawl.getInstance().getDataFolder(), "holograms.json");
        if (!file.exists()) {
            return;
        }
        try {

            List<SerializedHologram> loaded = Spartan.GSON.fromJson(FileUtils.readFileToString(file), new TypeToken<List<SerializedHologram>>() {
            }.getType());
            for (SerializedHologram serialized : loaded) {
                Hologram hologram = Holograms.newHologram().at(serialized.location).addLines(serialized.lines).build();
                this.holograms.put(serialized.id, hologram);
                hologram.send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Brawl.getInstance().getServer().getPluginManager().registerEvents(new HologramListener(), Brawl.getInstance());
    }

    public int register(Hologram hologram) {
        if ((hologram instanceof UpdatingHologram)) {
            throw new IllegalArgumentException("We can only serialize static holograms.");
        }
        int nextId = createId();
        this.holograms.put(nextId, hologram);
        save();
        return nextId;
    }

    public void save() {
        List<SerializedHologram> toSerialize = new ArrayList<>();
        for (Map.Entry<Integer, Hologram> entry : this.holograms.entrySet()) {
            toSerialize.add(new SerializedHologram(entry.getKey(), entry.getValue().getLocation(), entry.getValue().getLines()));
        }

        File file = new File(Brawl.getInstance().getDataFolder(), "holograms.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileUtils.write(file, Spartan.GSON.toJson(toSerialize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int createId() {
        int id = this.holograms.size() + 1;
        while (this.holograms.get(id) != null) {
            id++;
        }
        return id;
    }

    public void move(int id, Location location) {
        Hologram hologram = getHolograms().get(id);

        List<String> lines = hologram.getLines();

        this.holograms.remove(id);
        hologram.destroy();
        save();

        Hologram newHologram = Holograms.newHologram().at(location).addLines(lines).build();
        newHologram.send();
        this.holograms.put(id, newHologram);

        save();
    }

    @AllArgsConstructor
    private class SerializedHologram {

        int id;
        Location location;
        List<String> lines;

    }

}