package rip.thecraft.brawl.command.manage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.github.paperspigot.PaperSpigotConfig;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.AbilityHandler;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.listener.TestListener;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerCommand {

    @Command(names = "duelarena toggle", permission = "op")
    public static void arenaToggle(Player sender) {
        boolean newValue = !DuelArena.DISABLED;
        sender.sendMessage(ChatColor.GOLD + "You have " + (newValue ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GOLD + " duel arena");
        DuelArena.DISABLED = newValue;
    }

    @Command(names = "game toggle", permission = "op")
    public static void gameToggle(Player sender, GameType type) {
        boolean newValue = !type.isDisabled();
        sender.sendMessage(ChatColor.GOLD + "You have " + (newValue ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GOLD + " game " + ChatColor.WHITE + type.getShortName() + ChatColor.GOLD + ".");
        type.setDisabled(newValue);
    }

    @Command(names = "range", permission = "op", hidden = true)
    public static void range(Player player, Player target, @Param(defaultValue = "0") int range) {
        if (range <= 0) {
            player.sendMessage(ChatColor.RED + "Removed range from " + target.getName() + ".");
            TestListener.range.remove(target.getUniqueId());
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Set range for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " to: " + range + " blocks");
        TestListener.range.put(target.getUniqueId(), range);
    }

    @Command(names = "setmodifers strength", permission = "op")
    public static void setStrength(CommandSender sender, double newValue) {
        sender.sendMessage(ChatColor.GOLD + "Set strength modifier to: " + ChatColor.WHITE + newValue + ChatColor.GOLD + " from " + ChatColor.RED + PaperSpigotConfig.strengthEffectModifier + ChatColor.GOLD + "." + ChatColor.GRAY + " (Default: " + 1.3D + ")");
        PaperSpigotConfig.strengthEffectModifier = newValue;
    }

    @Command(names = "setmodifers weakness", permission = "op")
    public static void setWeakness(CommandSender sender, double newValue) {
        sender.sendMessage(ChatColor.GOLD + "Set weaknes modifier to: " + ChatColor.WHITE + newValue + ChatColor.GOLD + " from " + ChatColor.RED + PaperSpigotConfig.strengthEffectModifier + ChatColor.GOLD + "." + ChatColor.GRAY + " (Default: " + 1.3D + ")");
        PaperSpigotConfig.weaknessEffectModifier = newValue;
    }

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
