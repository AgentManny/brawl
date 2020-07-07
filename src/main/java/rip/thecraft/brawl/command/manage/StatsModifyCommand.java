package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;

public class StatsModifyCommand {

    @Command(names = { "statistics modify", "stats modify" }, description = "Modify a player's statistics", async = true, permission = "op")
    public static void execute(Player sender, PlayerData player, String statistic, double newValue) {
        StatisticType stat = StatisticType.parse(statistic);
        if (stat == null) {
            sender.sendMessage(ChatColor.RED + "Statistic " + statistic + " not found.");
            return;
        }

        PlayerStatistic playerStatistic = player.getStatistic();
        sender.sendMessage(ChatColor.GREEN + "Changed statistic " + stat.getColor() + stat.getName() + ChatColor.GREEN + " of " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + " to " + newValue + ". " + ChatColor.RED + "(from " + playerStatistic.get(stat) + ")");
        playerStatistic.set(stat, newValue);
    }
}
