package rip.thecraft.brawl.region;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.Spartan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RegionHandler implements Closeable {

    @Getter
    List<Region> regions = new ArrayList<>();

    public RegionHandler() {
        this.load();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Brawl.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;

                PacketContainer packet = event.getPacket();
                String sound = packet.getStrings().read(0);
                if (sound.contains("step") || sound.equals("random.bow")) {
                    Region region = get(player.getLocation());
                    if (region != null && region.getType() == RegionType.SAFEZONE) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }


    private void load() {
        File file = getFile();
        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(reader).getAsJsonArray();

            for (Object object : array) {
                JsonObject jsonObject = (JsonObject) object;
                Region region = new Region(jsonObject);
                this.regions.add(region);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.save();
    }

    public void save() {
        File file = getFile();

        try (FileWriter writer = new FileWriter(file)) {

            Spartan.GSON.toJson(toJson(), writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (Region region : this.regions) {
            jsonArray.add(region.toJson());
        }
        return jsonArray;
    }

    public void add(Region region) {
        this.regions.add(region);
        save();
    }

    public void remove(Region region) {
        regions.removeIf(region::equals);
        save();
    }

    public void remove(String name) {
        regions.removeIf(rg -> rg.getName().equalsIgnoreCase(name));
        save();
    }

    public Region get(Location location) {
        for (Region reg : regions) {
            if (reg.contains(location)) {
                return reg;
            }
        }
        return null;
    }

    public Region get(String name) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }
        return null;
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "regions.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public void close() {
        save();
    }
}