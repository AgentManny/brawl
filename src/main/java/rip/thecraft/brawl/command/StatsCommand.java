package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.command.manage.StatsModifyCommand;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;
import rip.thecraft.spartan.menu.menus.ConfirmMenu;

public class StatsCommand {

    @Command(names = { "statistics", "stats" }, description = "Shows a player's statistics", async = true)
    public static void execute(Player sender, @Param(defaultValue = "self") PlayerData player) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** Statistics of " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.DARK_PURPLE + " ***");
        for (StatisticType type : StatisticType.values()) {
            if (type.isHidden() || type == StatisticType.PRESTIGE) continue;
            double value = player.getStatistic().get(type);
            String displayValue = type.getFormatValue(value);
            if (type == StatisticType.LEVEL) {
                Level level = player.getLevel();
                displayValue = level.getSimplePrefix().replace("[", "").replace("]", "") + " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " EXP)";
            }
            sender.sendMessage(ChatColor.GRAY + type.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE +  displayValue);
        }
        sender.sendMessage(" ");
    }

    @Command(names = { "statistics reset", "stats reset" }, description = "Shows a player's statistics", async = true)
    public static void execute(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        PlayerStatistic statistic = playerData.getStatistic();
        int resetTokens = statistic.getResetTokens();
        if (resetTokens == 0) {
            sender.sendMessage(ChatColor.RED + "You don't have any reset tokens.");
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Are you sure you want to reset your statistics? " + ChatColor.RED.toString() + ChatColor.BOLD + "THIS CANNOT BE REVERSED!");
        new ConfirmMenu("Reset your statistics?", (callback) -> {
            if (callback) {
                if (statistic.getResetTokens() == 0) return;
                statistic.setResetTokens(statistic.getResetTokens() - 1);
                StatsModifyCommand.resetStats(null, playerData);
                sender.sendMessage(ChatColor.GREEN + "Your statistics has been cleared.");
            } else {
                sender.sendMessage(ChatColor.RED + "You cancelled your reset statistic token.");
            }
        }).openMenu(sender);
    }
}
