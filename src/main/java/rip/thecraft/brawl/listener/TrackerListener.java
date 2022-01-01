package rip.thecraft.brawl.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.spartan.Spartan;
import gg.manny.streamline.util.ItemBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TrackerListener implements Listener {

    private static int RANGE = 25;

    public static ItemStack TRACKER_ITEM;

    public TrackerListener(Brawl plugin) {
        // It updates every 5 seconds
        int UPDATE_TIME = 5;

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("last-tracker")) {
                        if (player.getInventory().contains(Material.COMPASS)) {
                            UUID lastTrackerId = UUID.fromString((String) player.getMetadata("last-tracker", plugin).value());
                            Player tracked = Bukkit.getPlayer(lastTrackerId);
                            if (tracked == null) {
                                player.removeMetadata("last-tracker", plugin);
                                player.sendMessage(ChatColor.RED + "Player Tracker: Lost signal to nearby player.");
                            } else {
                                player.setCompassTarget(tracked.getLocation());
                            }
                        }
                    }
                }
            }

        }.runTaskTimer(plugin, 20L, UPDATE_TIME * 20L);
    }

    static {
        TRACKER_ITEM = new ItemBuilder(Material.COMPASS)
                .name(ChatColor.AQUA + "Player Tracker")
                .lore(Collections.singletonList(ChatColor.GRAY + "Range: " + RANGE + " blocks"))
                .create();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (item.getType() == Material.COMPASS && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.AQUA + "Tracking: ")) {
                    if (!trackNearbyPlayer(player)) {
                        player.sendMessage(ChatColor.RED + "There isn't anyone within " + RANGE + " blocks to track.");
                    }
                }
            }
        }
    }

    private boolean trackNearbyPlayer(Player player) {
        List<Player> players = BrawlUtil.getNearbyPlayers(player, RANGE);
        if (!player.isEmpty()) {
            Player nearbyPlayer = players.get(Spartan.RANDOM.nextInt(players.size() - 1));
            player.sendMessage(ChatColor.YELLOW + "Now tracking: " + ChatColor.WHITE + nearbyPlayer.getDisplayName());
            player.setCompassTarget(nearbyPlayer.getLocation());
            player.setMetadata("last-tracker", new FixedMetadataValue(Brawl.getInstance(), nearbyPlayer.getUniqueId()));
            return true;
        }
        return false;

    }
}
