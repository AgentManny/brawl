package rip.thecraft.brawl.ability.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AbilityTasks {

    private Map<UUID, List<Integer>> repeatingTasks = new ConcurrentHashMap<>(); // These tasks will be removed as soon as player dies

    public void addTask(UUID uuid, BukkitTask task) {
        addTask(uuid, task.getTaskId());
    }

    public void addTask(UUID uuid, Integer taskId) {
        repeatingTasks.putIfAbsent(uuid, new ArrayList<>());
        repeatingTasks.get(uuid).add(taskId);
        debug(String.format("Added task %s to %s", taskId, MUUIDCache.name(uuid)));
    }

    public void removeTask(UUID uuid, BukkitTask task) {
        removeTask(uuid, task.getTaskId());
    }

    public void removeTask(UUID uuid, Integer taskId) {
        if (repeatingTasks.containsKey(uuid)) {
            List<Integer> tasks = repeatingTasks.get(uuid);
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            if (scheduler.isCurrentlyRunning(taskId)) {
                scheduler.cancelTask(taskId);
                debug(String.format("Cancelled active task %s from %s", taskId, MUUIDCache.name(uuid)));
            }
            tasks.remove(taskId);
            debug(String.format("Removed task %s from %s", taskId, MUUIDCache.name(uuid)));
            if (tasks.isEmpty()) {
                repeatingTasks.remove(uuid);
                debug(String.format("Removed as no tasks remain for %s", MUUIDCache.name(uuid)));
            }
        }
    }

    public void clear(UUID uuid) {
        if (repeatingTasks.containsKey(uuid)) {
            List<Integer> tasks = repeatingTasks.get(uuid);
            debug(String.format("Cancelled (forcefully) %s tasks: (%s)", MUUIDCache.name(uuid), tasks.size()));
            for (Integer task : tasks) {
                removeTask(uuid, task);
            }
        } else {
            debug(String.format("Tried to clear %s but has no tasks active", MUUIDCache.name(uuid)));
        }
    }

    private void debug(String message) {
        Player player = Bukkit.getPlayer("Mannys");
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "[Ability Tasks] " + message);
        }
    }

}