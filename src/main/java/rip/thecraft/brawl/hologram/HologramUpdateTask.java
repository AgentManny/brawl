package rip.thecraft.brawl.hologram;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.falcon.hologram.hologram.Hologram;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class HologramUpdateTask extends BukkitRunnable {

    private final HologramManager hologramManager;

    @Override
    public void run() {
        for (Map.Entry<UUID, Hologram> entry : hologramManager.getPlayerStats().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            Hologram hologram = entry.getValue();
            if (player == null) continue;

            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            hologram.setLines(hologramManager.getLines(playerData));

            hologram.send();
        }
    }

}
