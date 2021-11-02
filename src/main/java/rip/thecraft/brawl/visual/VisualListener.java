package rip.thecraft.brawl.visual;

import gg.manny.hologram.Hologram;
import gg.manny.hologram.HologramBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.thecraft.brawl.Brawl;

import static rip.thecraft.brawl.visual.VisualManager.HOLO_STATS;

@RequiredArgsConstructor
public class VisualListener implements Listener {

    private final VisualManager visualManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            if (getHoloStats() == null) return; // Prevent loading
            Hologram hologram = new HologramBuilder(player.getUniqueId())
                    .location(getHoloStats())
                    .addLines(visualManager.getHoloStats(player))
                    .build();
            visualManager.playerStats.put(player.getUniqueId(), hologram);
        }, 40L);
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
