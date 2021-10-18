package rip.thecraft.brawl.command.manage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityHandler;
import rip.thecraft.spartan.command.Command;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerCommand {

    @Command(names = "tasks", permission = "op")
    public static void ongoingTasks(CommandSender sender) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        List<BukkitTask> pendingTasks = scheduler.getPendingTasks();
        List<BukkitWorker> activeWorkers = scheduler.getActiveWorkers();
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.GOLD + "Pending tasks (" + ChatColor.YELLOW + pendingTasks.size() + ChatColor.GOLD + "):");
        Map<Plugin, Integer> pluginTasks = new HashMap<>();
        for (BukkitTask task : pendingTasks) {
            Plugin owner = task.getOwner();
            pluginTasks.put(owner, pluginTasks.getOrDefault(owner, 0) + 1);
        }
        pluginTasks.forEach((plugin, tasks) -> sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.YELLOW + tasks + "x " + ChatColor.GOLD + plugin.getName()));

        AbilityHandler abilityHandler = Brawl.getInstance().getAbilityHandler();
        Map<Ability, Integer> abilityTasks = new HashMap<>();
        int totalTasks = 0;
        for (Ability ability : abilityHandler.getAbilities().values()) {
            Collection<ConcurrentLinkedQueue<Integer>> tasks = ability.getTasks().getRepeatingTasks().values();
            int totalAbilityTasks = 0;
            for (ConcurrentLinkedQueue<Integer> task : tasks) {
                totalAbilityTasks += task.size();
            }
            totalTasks += totalAbilityTasks;
            if (totalAbilityTasks != 0) {
                abilityTasks.put(ability, totalAbilityTasks);
            }
        }

        sender.sendMessage(ChatColor.GOLD + "Ability tasks (" + ChatColor.YELLOW + totalTasks + ChatColor.GOLD + "):");
        if (abilityTasks.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are no tasks active.");
        }
        abilityTasks.forEach((ability, tasks) -> sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.YELLOW + tasks + "x " + ChatColor.GOLD + ability.getName()));
    }

}
