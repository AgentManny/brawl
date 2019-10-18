package gg.manny.brawl.warp;

import com.google.gson.reflect.TypeToken;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.warp.command.WarpCommand;
import gg.manny.pivot.Pivot;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WarpManager {

    @Getter
    private Map<String, Warp> warps = new HashMap<>();

    public WarpManager() {
        File file = new File(Brawl.getInstance().getDataFolder(), "warps.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Pivot.getInstance().getQuantum().registerParameterType(Warp.class, new WarpTypeAdapter(this));
        Pivot.getInstance().getQuantum().registerCommand(new WarpCommand(this));

        try {
            List<Warp> warps = Brawl.GSON.fromJson(FileUtils.readFileToString(file), new TypeToken<List<Warp>>() {}.getType());
            if (warps != null) {
                warps.forEach(warp -> this.warps.put(warp.getName().toLowerCase(), warp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Warp getWarp(String warpName) {
        return this.warps.get(warpName.toLowerCase());
    }

    public Warp createWarp(String warpName, Location location, Kit kit) {
        Warp warp = new Warp(warpName, location, kit == null ? null : kit.getName());
        this.warps.put(warpName.toLowerCase(), warp);
        return warp;
    }

    public void removeWarp(String warpName) {
        this.warps.remove(warpName.toLowerCase());
    }

    public void save() {
        List<Warp> toSerialize = new ArrayList<>();
        for (Map.Entry<String, Warp> entry : this.warps.entrySet()) {
            toSerialize.add(new Warp(entry.getKey(), entry.getValue().getLocation(), entry.getValue().getKit()));
        }

        File file = new File(Brawl.getInstance().getDataFolder(), "warps.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileUtils.write(file, Brawl.GSON.toJson(toSerialize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
