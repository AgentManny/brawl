package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;
import rip.thecraft.brawl.spectator.SpectatorMode;

import java.util.List;

/**
 * Created by Flatfile on 10/19/2021.
 */
public class SpectatorScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        SpectatorMode spectatorMode = plugin.getSpectatorManager().getSpectator(player);
        SpectatorMode.SpectatorType spectating = spectatorMode.getSpectating();

        lines.add(ChatColor.DARK_PURPLE.toString() + ChatColor.ITALIC + "[Spectator Mode]");

        switch (spectating){
            case GAME: {
                Game game = spectatorMode.getGame();
                if (game != null) {
                    lines.addAll(game.getSidebar(player));
                }
                break;
            }
            case MATCH: {
                Match match = spectatorMode.getMatch();
                if (match != null) {
                    lines.add(ChatColor.WHITE + "Players: " + ChatColor.DARK_PURPLE + Bukkit.getPlayer(match.getPlayer1()).getName() +
                            ChatColor.WHITE + " vs " + ChatColor.DARK_PURPLE + Bukkit.getPlayer(match.getPlayer2()).getName());
                    lines.add(ChatColor.WHITE + "Queue Type: " + ChatColor.DARK_PURPLE + match.getQueueType().getName());
                }
                break;
            }
            case SPAWN:{
                lines.add(ChatColor.WHITE + "Setting: " + ChatColor.DARK_PURPLE + spectating.getName());
                lines.add(ChatColor.WHITE + "Online: " + ChatColor.DARK_PURPLE + Bukkit.getOnlinePlayers().size());
                break;
            }
            case PLAYER:{
                lines.add(ChatColor.WHITE + "Setting: " + ChatColor.DARK_PURPLE + spectating.getName());
                lines.add(ChatColor.WHITE + "Player: " + ChatColor.DARK_PURPLE + spectatorMode.getSpectatedPlayer().getName());
                break;
            }
            case DUEL_ARENA:{
                lines.add(ChatColor.WHITE + "Active Duels: " + ChatColor.DARK_PURPLE + Brawl.getInstance().getMatchHandler().getMatches().size());
                lines.add(ChatColor.WHITE + "Ranked Queue: " + ChatColor.DARK_PURPLE + Brawl.getInstance().getMatchHandler().getRankedQueue().size());
                lines.add(ChatColor.WHITE + "Unranked Queue: " + ChatColor.DARK_PURPLE + Brawl.getInstance().getMatchHandler().getUnrankedQueue().size());
                break;
            }

            default:{

            }
        }

        return lines;
    }

}
