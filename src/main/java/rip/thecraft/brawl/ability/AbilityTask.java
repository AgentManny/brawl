package rip.thecraft.brawl.ability;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;

public abstract class AbilityTask extends BukkitRunnable {

    /** Returns the player who executed the task */
    protected final Player player;

    /** Returns the duration task should be active for */
    private final long duration;

    /** Returns the time the task should repeat for (e.g. 20L = 1 second) */
    private final long ticks;

    /** Returns when the task has started */
    private long startedAt = -1;

    protected AbilityTask(Player player, long duration, long ticks) {
        this.player = player;
        this.duration = duration;
        this.ticks = ticks;
    }

    public abstract void onTick();

    public abstract void onCancel();

    /** Whether external factors should cancel the task */
    public boolean shouldCancel() {
        return false;
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - startedAt > duration || player == null || shouldCancel()) {
            onCancel();
            cancel();
            return;
        }

        onTick();
    }

    public synchronized void start() {
        startedAt = System.currentTimeMillis();
        runTaskTimer(Brawl.getInstance(), ticks, ticks);
    }

    @Override
    public synchronized BukkitTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        return super.runTask(plugin);
    }
}
