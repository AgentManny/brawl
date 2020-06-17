package rip.thecraft.brawl.visual.tasks;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.visual.VisualManager;
import rip.thecraft.falcon.hologram.hologram.Hologram;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class HologramUpdateTask extends BukkitRunnable {

    private final VisualManager visualManager;

    @Override
    public void run() {
        for (Map.Entry<UUID, Hologram> entry : visualManager.playerStats.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            Hologram hologram = entry.getValue();
            if (player == null) continue;

            hologram.setLines(visualManager.getHoloStats(player));
            hologram.send();
        }
    }

}
