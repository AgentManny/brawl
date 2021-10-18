package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DuelLobbyScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        PlayerStatistic statistic = playerData.getStatistic();
        Level level = playerData.getLevel();

        lines.add(ChatColor.WHITE + "Level: " + level.getDisplayName());
        lines.add(ChatColor.WHITE + "Required XP: " + ChatColor.LIGHT_PURPLE + (level.getMaxExperience() - level.getCurrentExp()));
        lines.add("  ");
        lines.add(ChatColor.WHITE + "Duel Wins: " + ChatColor.GREEN + (int)statistic.get(StatisticType.DUEL_WINS));
        lines.add(ChatColor.WHITE + "Duel Losses: " + ChatColor.RED + (int)statistic.get(StatisticType.DUEL_LOSSES));
        lines.add("  ");
        lines.add(ChatColor.WHITE + "Winstreak: " + ChatColor.LIGHT_PURPLE + (int)statistic.get(StatisticType.DUEL_WINS));
        if (plugin.getMatchHandler().isInQueue(player)) {
            lines.add("    ");
            lines.add(ChatColor.LIGHT_PURPLE + plugin.getMatchHandler().getFriendlyQueue(player));
            lines.add(ChatColor.WHITE + "Time: " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoMMSS((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerData.getQueueData().getQueueTime())));

//            QueueSearchTask task = playerData.getQueueData().getTask();
//            if (task != null) {
//                lines.add(ChatColor.DARK_PURPLE + "Elo range: " + ChatColor.LIGHT_PURPLE + "[" + task.getMinRange() + " -> " + task.getMaxRange() + "]");
//            }
        }
        return lines;
    }

}