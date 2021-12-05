package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;

public class StatsModifyCommand {

    @Command(names = { "statistics modify", "stats modify" }, description = "Modify a player's statistics", async = true, permission = "op")
    public static void execute(CommandSender sender, PlayerData player, String statistic, double newValue) {
        StatisticType stat = StatisticType.parse(statistic);
        if (stat == null) {
            sender.sendMessage(ChatColor.RED + "Statistic " + statistic + " not found.");
            return;
        }

        PlayerStatistic playerStatistic = player.getStatistic();
        sender.sendMessage(ChatColor.GREEN + "Changed statistic " + stat.getColor() + stat.getName() + ChatColor.GREEN + " of " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + " to " + newValue + ". " + ChatColor.RED + "(from " + playerStatistic.get(stat) + ")");
        playerStatistic.set(stat, newValue);
        player.save();
    }

    @Command(names = { "statistics add", "stats add" }, description = "Add to a statistic a player's statistics", async = true, permission = "op")
    public static void addStats(CommandSender sender, PlayerData player, String statistic, double newValue) {
        StatisticType stat = StatisticType.parse(statistic);
        if (stat == null) {
            sender.sendMessage(ChatColor.RED + "Statistic " + statistic + " not found.");
            return;
        }

        PlayerStatistic playerStatistic = player.getStatistic();
        sender.sendMessage(ChatColor.GREEN + "Added statistic " + stat.getColor() + stat.getName() + ChatColor.GREEN + " " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ": +" + newValue);
        playerStatistic.add(stat, newValue);

        ChatColor color = stat.getColor();
        Player player1 = player.getPlayer();
        if (player1 != null) {
            player1.sendMessage(color + " + " + ChatColor.BOLD + (int)newValue + color + " " + stat.getName());
        }
        player.save();
    }

    @Command(names = { "statistics addresettoken" }, description = "Asdd a reset token to a player's statistics", async = true, permission = "op")
    public static void addResetToken(CommandSender sender, PlayerData player) {
        PlayerStatistic playerStatistic = player.getStatistic();
        playerStatistic.setResetTokens(playerStatistic.getResetTokens() + 1);
        player.save();
        player.fetchPlayer().ifPresent(data -> data.sendMessage(ChatColor.LIGHT_PURPLE + " + " + ChatColor.BOLD + "1 " + ChatColor.LIGHT_PURPLE + "Reset Statistic Token"));
        sender.sendMessage(ChatColor.GREEN + "Added statistic reset token to " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".");
    }

    @Command(names = { "statistics wipe", "stats wipe" }, description = "Reset a player's statistics", async = true, permission = "op")
    public static void resetStats(CommandSender sender, PlayerData player) {
        PlayerStatistic playerStatistic = player.getStatistic();
        for (StatisticType statistic : StatisticType.values()) {
            playerStatistic.set(statistic, statistic.getDefaultValue());
        }
        for (KitStatistic value : player.getStatistic().getKitStatistics().values()) {
            value.reset();
        }
        playerStatistic.getGameStatistics().forEach((game, stat) -> {
            stat.setPlayed(0);
            stat.setLosses(0);
            stat.setWins(0);
            stat.getProperties().clear();
        });
        player.getLevel().setCurrentExp(0);
        player.save();
        if (sender != null) { // I use this for reset tokens.
            sender.sendMessage(ChatColor.GREEN + "Reset player statistics of " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".");
        }
    }
}
