package rip.thecraft.brawl.kit.ability.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.player.PlayerData;

import java.util.UUID;

public abstract class AbilityTask extends BukkitRunnable {

    /** Returns the ability using task */
    protected final Ability ability;

    /** Returns the player who executed the task */
    protected final Player player;

    protected final UUID playerId;

    /** Returns the duration task should be active for */
    private final long duration;

    /** Returns the time the task should repeat for (e.g. 20L = 1 second) */
    private final long ticks;

    /** Returns when the task has started */
    private long startedAt = -1;

    protected AbilityTask(Ability ability, Player player, long duration, long ticks) {
        this.ability = ability;
        this.player = player;
        this.playerId = player.getUniqueId();
        this.duration = duration;
        this.ticks = ticks;
    }

    public abstract void onTick();

    public abstract void onCancel();

    /** Whether external factors should cancel the task */
    public boolean shouldCancel() {
        if (player != null) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            return playerData.isSpawnProtection(); // Don't allow tasks to inflict spawn
        }
        return false;
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - startedAt > duration || player == null || shouldCancel()) {
            ability.getTasks().removeTask(playerId, getTaskId());
            onCancel();
            cancel();
            return;
        }

        onTick();
    }

    public synchronized void start() {
        startedAt = System.currentTimeMillis();
        BukkitTask bukkitTask = runTaskTimer(Brawl.getInstance(), ticks, ticks);
        ability.getTasks().addTask(playerId, bukkitTask);
    }

    @Override
    public synchronized BukkitTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        return super.runTask(plugin);
    }
}
