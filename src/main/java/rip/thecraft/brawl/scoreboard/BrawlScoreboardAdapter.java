package rip.thecraft.brawl.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.scoreboard.type.DuelLobbyScoreboardProvider;
import rip.thecraft.brawl.scoreboard.type.GameLobbyScoreboardProvider;
import rip.thecraft.brawl.scoreboard.type.MatchScoreboardProvider;
import rip.thecraft.brawl.scoreboard.type.SpawnScoreboardProvider;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.spartan.scoreboard.ScoreboardAdapter;
import rip.thecraft.spartan.util.LinkedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrawlScoreboardAdapter implements ScoreboardAdapter {

    private static final String SPACERS = "     ";

    private final Brawl plugin;

    private Map<PlayerState, ScoreboardProvider> providers = new HashMap<>();

    public BrawlScoreboardAdapter(Brawl plugin) {
        this.plugin = plugin;

        providers.put(PlayerState.SPAWN, new SpawnScoreboardProvider());
        providers.put(PlayerState.GAME_LOBBY, new GameLobbyScoreboardProvider());
        providers.put(PlayerState.ARENA, new DuelLobbyScoreboardProvider());
        providers.put(PlayerState.MATCH, new MatchScoreboardProvider());
    }

    @Override
    public String getTitle(Player player) {
        return SPACERS + ChatColor.DARK_PURPLE + ChatColor.BOLD + "KAZE" + ChatColor.GRAY + " " + Brawl.getVersion() + SPACERS;
    }

    @Override
    public void getLines(LinkedList<String> lines, Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        if (playerData == null) return;

        lines.add("      ");

        switch (playerData.getPlayerState()) {
            case SPECTATING: {
                this.getSpectatorMode(player, playerData, lines);
                break;
            }
            case GAME: {
                Game game = plugin.getGameHandler().getActiveGame();
                if (game != null) {
                    lines.addAll(game.getSidebar(player));
                }
                break;
            }
            case GAME_LOBBY: {
                providers.get(PlayerState.GAME_LOBBY).getLines(player, playerData, lines);
                break;
            }
            case MATCH: {
                providers.get(PlayerState.MATCH).getLines(player, playerData, lines);
                break;
            }
            case ARENA: {
                providers.get(PlayerState.ARENA).getLines(player, playerData, lines);
                break;
            }
            default: {
                providers.get(PlayerState.SPAWN).getLines(player, playerData, lines);
                break;
            }
        }
        lines.add("   ");
        lines.add(ChatColor.LIGHT_PURPLE + "kaze.gg");
    }

    private void getSpectatorMode(Player player, PlayerData playerData, List<String> lines) {
        SpectatorMode spectatorMode = plugin.getSpectatorManager().getSpectator(player);
        SpectatorMode.SpectatorType spectating = spectatorMode.getSpectating();

        lines.add(ChatColor.DARK_PURPLE.toString() + ChatColor.ITALIC + "[Spectator Mode]");
        lines.add(ChatColor.GRAY + spectating.getName());
//
//        if (spectatorMode.getFollow() != null) {
//            Player follow = Bukkit.getPlayer(spectatorMode.getFollow());
//            if (follow != null) {
//                lines.add(ChatColor.DARK_PURPLE + "Following: " + ChatColor.LIGHT_PURPLE + follow.getName());
//            }
//        }
        switch (spectating) {
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
                }
                // todo add spectator scoreboard stuff
                break;
            }
            default: {

                //lines.addAll(getKazeSpawn(player, playerData, lines));
            }
        }
    }
}