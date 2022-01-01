package rip.thecraft.brawl.spawn.levels.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.spawn.levels.Level;

@RequiredArgsConstructor
public class LevelFlashTask extends BukkitRunnable {

    private final Player player;
    private final Level level;

    private int count = 0;
    private boolean full = false;

    @Override
    public void run() {
        if (count++ < 5) {
            full = !full;
            player.setExp(full ? 1F : 0F);
        } else {
            cancelTask();
        }
    }

    public void cancelTask() {
        cancel();
        level.updateExp(player);
    }
}
