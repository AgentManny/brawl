package gg.manny.brawl.command;

import com.google.common.base.Strings;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.util.chatcolor.CC;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StatsCommand {

    @Command(names = { "statistics", "stats" }, description = "Shows a player's statistics", async = true)
    public void execute(Player sender, @Parameter(value = "self") PlayerData player) {
        String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.YELLOW + "Stats for " + ChatColor.LIGHT_PURPLE + player.getName());
        sender.sendMessage(line);
        for (StatisticType type : StatisticType.values()) {
            sender.sendMessage(ChatColor.YELLOW + type.getName() + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + Math.round(player.getStatistic().get(type)));
        }
        sender.sendMessage(line);
    }

}
