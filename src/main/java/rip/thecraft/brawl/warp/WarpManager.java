package rip.thecraft.brawl.warp;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.spartan.Spartan;

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

        try {
            List<Warp> warps = Spartan.GSON.fromJson(FileUtils.readFileToString(file), new TypeToken<List<Warp>>() {}.getType());
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

    public Warp createWarp(String warpName, Location location, Kit kit, boolean enabled) {
        Warp warp = new Warp(warpName, location, kit == null ? null : kit.getName(), enabled);
        this.warps.put(warpName.toLowerCase(), warp);
        return warp;
    }

    public void removeWarp(String warpName) {
        this.warps.remove(warpName.toLowerCase());
    }

    public void save() {
        List<Warp> toSerialize = new ArrayList<>();
        for (Map.Entry<String, Warp> entry : this.warps.entrySet()) {
            toSerialize.add(new Warp(entry.getKey(), entry.getValue().getLocation(), entry.getValue().getKit(), entry.getValue().isEnabled()));
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
            FileUtils.write(file, Spartan.GSON.toJson(toSerialize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
