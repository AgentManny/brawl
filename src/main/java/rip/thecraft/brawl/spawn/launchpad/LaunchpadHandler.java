package rip.thecraft.brawl.spawn.launchpad;

import com.google.common.reflect.TypeToken;
import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.Spartan;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static rip.thecraft.spartan.Spartan.GSON;

public class LaunchpadHandler {

    public static final String JUMP_METADATA = "JUMP";
    public static final int LAUNCHPAD_OFFSET_RADIUS = 15;

    private Brawl plugin;

    @Getter private List<Location> locations = new ArrayList<>();

    @Getter private Map<UUID, Location> pendingJumps = new HashMap<>();

    public LaunchpadHandler(Brawl plugin) {
        this.plugin = plugin;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin.getServer().getPluginManager().registerEvents(new LaunchpadListener(this, plugin), plugin);
        plugin.getCommandService().register(new LaunchpadCommand(), "Launchpad", "lp", "jump");
    }

    public Location getRandomLocation() {
        if (plugin.getServer().getOnlinePlayers().size() > 75) {
            return locations.get(Brawl.RANDOM.nextInt(locations.size() - 1));
        }
        List<Location> hotspots = new ArrayList<>();
        for (Location location : locations) {
            Collection<Player> nearbyPlayers = location.getNearbyPlayers(35);
            for (Map.Entry<UUID, Location> entry : pendingJumps.entrySet()) {
                if (entry.getValue().equals(location)) {
                    Player player = plugin.getServer().getPlayer(entry.getKey());
                    if (player != null && !nearbyPlayers.contains(player)) {
                        nearbyPlayers.add(player);
                    }
                }
            }
            if (!nearbyPlayers.isEmpty()) {
                hotspots.add(location);
            }
        }
        if (hotspots.size() > 3 || (!hotspots.isEmpty() && plugin.getServer().getOnlinePlayers().size() <= 12)) { // Ensure there are more than 1 hotspot
            return hotspots.get(Brawl.RANDOM.nextInt(hotspots.size() - 1));
        }
        return locations.get(Brawl.RANDOM.nextInt(locations.size() - 1));
    }

    public Location getOptimalLocation(Location origin) {
        Location min = origin.clone().subtract(LAUNCHPAD_OFFSET_RADIUS, 0, LAUNCHPAD_OFFSET_RADIUS);
        Location max = origin.clone().add(LAUNCHPAD_OFFSET_RADIUS, 0, LAUNCHPAD_OFFSET_RADIUS);
        Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), min.getY(), Math.abs(max.getZ() - min.getZ()));
        Location randomLoc = new Location(min.getWorld(), (Math.random() * range.getX()) + (Math.min(min.getX(), max.getX())), range.getY(), (Math.random() * range.getZ()) + (Math.min(min.getZ(), max.getZ())));
        Block highestBlockAt = min.getWorld().getHighestBlockAt(randomLoc);
        Location optimalLoc = highestBlockAt.getLocation();
        if (optimalLoc.getBlock().isLiquid()) {
            return origin;
        }
        return optimalLoc;
    }

    public Optional<Location> getLaunchpad(Location location) {
        return locations.stream().filter(loc ->
                loc.getBlockX() == location.getBlockX() &&
                        loc.getBlockY() == location.getBlockY() &&
                        loc.getBlockZ() == location.getBlockZ()
        ).findAny();
    }

    /**
     * Loads launchpads from disk
     */
    public void load() throws Exception {
        plugin.getLogger().info("[Launchpad] Loading launchpads...");
        File file = getFile();
        @Cleanup FileReader reader = new FileReader(file);
        List<Location> locations = Spartan.GSON.fromJson(reader, new TypeToken<List<Location>>(){}.getType());
        if (locations != null) {
            this.locations = locations;
        }
        plugin.getLogger().info("[Launchpad] Loaded (" + this.locations.size() + ")");
        save();
    }

    /**
     * Saves launchpads to disk
     */
    public void save() {
        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(locations));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "launchpads.json");
        if (!file.exists()) {
            try {
                plugin.getLogger().info("[Launchpad] Creating launchpads.json file...");
                file.createNewFile();

            } catch (IOException ignored) {
            }
        }
        return file;
    }

}
