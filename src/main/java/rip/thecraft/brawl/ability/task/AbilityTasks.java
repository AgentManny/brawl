package rip.thecraft.brawl.ability.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class AbilityTasks {

    private Map<UUID, ConcurrentLinkedQueue<Integer>> repeatingTasks = new ConcurrentHashMap<>(); // These tasks will be removed as soon as player dies

    private Map<Location, BlockState> storedBlocks = new ConcurrentHashMap<>();

    public void addTask(UUID uuid, BukkitTask task) {
        addTask(uuid, task.getTaskId());
    }

    public void addTask(UUID uuid, Integer taskId) {
        repeatingTasks.putIfAbsent(uuid, new ConcurrentLinkedQueue<>());
        repeatingTasks.get(uuid).add(taskId);
    }

    public void removeTask(UUID uuid, BukkitTask task) {
        removeTask(uuid, task.getTaskId());
    }

    public void removeTask(UUID uuid, Integer taskId) {
        if (repeatingTasks.containsKey(uuid)) {
            ConcurrentLinkedQueue<Integer> tasks = repeatingTasks.get(uuid);
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            if (scheduler.isCurrentlyRunning(taskId)) {
                scheduler.cancelTask(taskId);
            }
            tasks.remove(taskId);
            if (tasks.isEmpty()) {
                repeatingTasks.remove(uuid);
            }
        }
    }

    public void clear(UUID uuid) {
        if (repeatingTasks.containsKey(uuid)) {
            ConcurrentLinkedQueue<Integer> tasks = repeatingTasks.get(uuid);
            for (Integer task : tasks) {
                removeTask(uuid, task);
            }
        }
    }
}