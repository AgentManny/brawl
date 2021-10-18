package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;

import java.util.List;
import java.util.Map;

public class GameLobbyScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        GameLobby lobby = plugin.getGameHandler().getLobby();
        lines.add(ChatColor.WHITE + "Game: " + ChatColor.LIGHT_PURPLE + lobby.getGameType().getShortName());
        lines.add(" ");
        lines.add(ChatColor.WHITE + "Players: " + ChatColor.LIGHT_PURPLE + lobby.getPlayers().size() + "/" + lobby.getGameType().getMaxPlayers());
        lines.add(ChatColor.WHITE + "Starting in " + ChatColor.LIGHT_PURPLE + lobby.getStartTime() + "s");
        lines.add("  ");
        lines.add(ChatColor.WHITE + "Map Votes:");
        int i = 0;
        for (Map.Entry<String, Integer> entry : lobby.getSortedVotes().entrySet()) {
            lines.add(ChatColor.LIGHT_PURPLE + " ● " + ChatColor.WHITE + entry.getKey() + " " + ChatColor.LIGHT_PURPLE + entry.getValue());
            if (i++ == 5) break;
        }
        return lines;
    }

}