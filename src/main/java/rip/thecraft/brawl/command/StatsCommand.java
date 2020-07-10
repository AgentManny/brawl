package rip.thecraft.brawl.command;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class StatsCommand {

    @Command(names = { "statistics", "stats" }, description = "Shows a player's statistics", async = true)
    public static void execute(Player sender, @Param(defaultValue = "self") PlayerData player) {
        String line = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.YELLOW + "Stats for " + ChatColor.LIGHT_PURPLE + player.getName());
        sender.sendMessage(line);
        for (StatisticType type : StatisticType.values()) {
            if (type.isHidden()) continue;
            String displayValue =  String.valueOf(Math.round(player.getStatistic().get(type)));
            if (type == StatisticType.LEVEL) {
                Level level = player.getLevel();
                displayValue += " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " EXP)";
            }
            sender.sendMessage(ChatColor.YELLOW + type.getName() + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE +  displayValue);
        }
        sender.sendMessage(line);
    }
}
