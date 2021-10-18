package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.ScoreboardHandler;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;
import rip.thecraft.brawl.util.DurationFormatter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpawnScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        PlayerStatistic statistic = playerData.getStatistic();
        Kit kit = playerData.getSelectedKit();
        Level level = playerData.getLevel();

        lines.add(ChatColor.WHITE + "Level: " + level.getDisplayName());
        lines.add(ChatColor.WHITE + "Required XP: " + ChatColor.LIGHT_PURPLE + (level.getMaxExperience() - level.getCurrentExp()));
        lines.add(ChatColor.WHITE + "Credits: " + ChatColor.GOLD + (int) statistic.get(StatisticType.CREDITS));
        lines.add("  ");
        if (kit != null) {
            lines.add(ChatColor.WHITE + "Kit: " + ChatColor.LIGHT_PURPLE + kit.getName());
            for (Ability ability : kit.getAbilities()) {
                if (ability instanceof ScoreboardHandler) {
                    ((ScoreboardHandler) ability).getScoreboard(player).forEach((key, value) -> lines.add(ChatColor.WHITE + key + ": " + ChatColor.YELLOW + value));
                }
                if (ability.hasCooldown(playerData.getPlayer(), false)) {
                    lines.add(ChatColor.WHITE + ability.getName() + ": " + ChatColor.RED + DurationFormatter.getRemaining(ability.toCooldown(playerData).getRemaining()));
                }
            }
            lines.add(ChatColor.BLACK + " ");
        }
        if (plugin.getEventHandler().getActiveKOTH() != null) {
            lines.addAll(plugin.getEventHandler().getActiveKOTH().getScoreboard(player));
        }
        if (playerData.hasCombatLogged()) {
            lines.add(ChatColor.WHITE + "Combat: " + ChatColor.RED + TimeUnit.MILLISECONDS.toSeconds(playerData.getCombatTaggedTil() - System.currentTimeMillis()) + "s");
        }

        lines.add(ChatColor.WHITE + "Killstreak: " + ChatColor.RED + (int) statistic.get(StatisticType.KILLSTREAK));
        if (playerData.hasCooldown("ENDERPEARL")) {
            lines.add(ChatColor.WHITE + "Enderpearl: " + ChatColor.RED + DurationFormatter.getRemaining(playerData.getCooldown("ENDERPEARL").getRemaining()));
        }
        return lines;
    }

}