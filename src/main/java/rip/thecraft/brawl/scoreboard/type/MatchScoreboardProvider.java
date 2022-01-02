package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.MatchState;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.List;

public class MatchScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        Match match = plugin.getMatchHandler().getMatch(player);
        lines.add(ChatColor.WHITE + "Kit: " + ChatColor.LIGHT_PURPLE + (match.getKit() != null && match.getState() == MatchState.GRACE_PERIOD ? "???" : match.getLoadout().getName()));
        if (match.getState() == MatchState.FINISHED) {
            lines.add(ChatColor.WHITE + "Winner: " + ChatColor.LIGHT_PURPLE + match.getWinnerName());
        } else {
            if (match.getState() == MatchState.GRACE_PERIOD) {
                lines.add(ChatColor.WHITE + "Opponent: " + ChatColor.LIGHT_PURPLE + MUUIDCache.name(match.getOpposite(player.getUniqueId())));
            }

            Player opponent = Bukkit.getPlayer(match.getOpposite(player.getUniqueId()));
            if (opponent != null) {
                lines.add("  ");
                lines.add(ChatColor.WHITE + "Your Ping: " + ChatColor.LIGHT_PURPLE + ((CraftPlayer)player).getHandle().ping + "ms");
                lines.add(ChatColor.WHITE + "Their Ping: " + ChatColor.LIGHT_PURPLE + ((CraftPlayer)opponent).getHandle().ping + "ms");
            }
        }
        return lines;
    }

}