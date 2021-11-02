package rip.thecraft.brawl.visual;

import gg.manny.hologram.Hologram;
import gg.manny.hologram.HologramBuilder;
import gg.manny.hologram.HologramPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;

import static rip.thecraft.brawl.visual.VisualManager.HOLO_STATS;

@RequiredArgsConstructor
public class VisualListener implements Listener {

    private final VisualManager visualManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            if (getHoloStats() != null) {
                Hologram hologram = new HologramBuilder(player.getUniqueId())
                        .location(getHoloStats())
                        .addLines(visualManager.getHoloStats(player))
                        .build();
                hologram.sendTo(player);
                visualManager.playerStats.put(player.getUniqueId(), hologram);
            }
        }, 15L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (visualManager.playerStats.containsKey(player.getUniqueId())) {
                    visualManager.playerStats.get(player.getUniqueId()).sendTo(player);
                }
            }
        }.runTaskLater(HologramPlugin.getInstance(), 15L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        visualManager.playerStats.get(player.getUniqueId()).destroy();
        visualManager.playerStats.remove(player.getUniqueId());
    }

    private Location getHoloStats() {
        return Brawl.getInstance().getLocationByName(HOLO_STATS);
    }
}
