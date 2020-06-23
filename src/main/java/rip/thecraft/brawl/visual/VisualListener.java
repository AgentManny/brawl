package rip.thecraft.brawl.visual;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.falcon.hologram.hologram.Hologram;
import rip.thecraft.falcon.hologram.hologram.Holograms;

import java.util.Arrays;

import static rip.thecraft.brawl.visual.VisualManager.HOLO_STATS;

@RequiredArgsConstructor
public class VisualListener implements Listener {

    private final VisualManager visualManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (getHoloStats() == null) return; // Prevent loading
        Hologram hologram = Holograms.forPlayers(Arrays.asList(player))
                .at(getHoloStats())
                .addLines(visualManager.getHoloStats(player))
                .build();
        hologram.send();
        visualManager.playerStats.put(player.getUniqueId(), hologram);
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