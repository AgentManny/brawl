package rip.thecraft.brawl.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.game.team.GameTeam;
import rip.thecraft.brawl.game.type.PartneredGame;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.spectator.SpectatorMode;

import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    private static EntityHider entityHider = Brawl.getInstance().getEntityHider();

    private static void debug(Player observer, ChatColor color, String action, Player target) {
        Player player = Bukkit.getPlayer("Mannys");
        if (player != null) {
            player.sendMessage(ChatColor.GRAY + "[Visibility] " + ChatColor.WHITE + observer.getName() + ChatColor.GRAY + " is " + color + action + ChatColor.GRAY + " to: " + target.getName());
        }
    }

    public static void updateVisibility(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other == player) continue;

            if (!shouldSee(other, player)) {
                entityHider.hideEntity(other, player);
                // entityHider.hideEntity(other, player);
            } else {
                entityHider.showEntity(other, player);
            }
        }
    }

    public static boolean shouldSee(Player target, Player viewer) {
        if (viewer == target) return true;
        UUID observerId = viewer.getUniqueId();
        UUID targetId = target.getUniqueId();

        DuelArenaHandler arenaHandler = Brawl.getInstance().getMatchHandler();
        SpectatorManager spectatorManager = Brawl.getInstance().getSpectatorManager();
        GameHandler gameHandler = Brawl.getInstance().getGameHandler();

        Match observerMatch = arenaHandler.containsPlayer(viewer, true);
        Match targetMatch = arenaHandler.containsPlayer(viewer, true);

        GameLobby lobby = gameHandler.getLobby();
        Game game = gameHandler.getActiveGame();

        SpectatorMode observerSpectator = spectatorManager.getSpectator(viewer);
        SpectatorMode targetSpectator = spectatorManager.getSpectator(target);
        if (observerSpectator != null) {
            if (observerSpectator.isShowSpectators() && targetSpectator != null) {
                if (observerMatch != null && targetMatch != null) {
                    return observerMatch == targetMatch;
                } else if (game != null && game.getSpectators().contains(observerId) && game.getSpectators().contains(targetId)) {
                    return true;
                }
                return observerSpectator.getSpectating() == targetSpectator.getSpectating();
            }

            return false;
        } else if (game != null && game.containsPlayer(viewer) && game.containsPlayer(target)) {
            if (!(game.getState() == GameState.FINISHED || game.getState() == GameState.ENDED)) {
                if (game.getSpectators().contains(targetId) && !game.getSpectators().contains(observerId)) {
                    return false;
                }

                if (game.getSpectators().contains(targetId) && game.getSpectators().contains(observerId)) {
                    return true;
                }

                if (game instanceof PartneredGame) {
                    PartneredGame partneredGame = (PartneredGame) game;

                    GameTeam<GamePlayer> team = ((PartneredGame) game).getTeam(target);
                    return team == null || team.getGamePlayer(target).isAlive();
                }
                return true;
            }
        }  else {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(viewer);
            PlayerState playerState = playerData.getPlayerState();

            PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);
            PlayerState targetState = targetData.getPlayerState();
//            Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Processed with " + VisibilityHandler.OTHER.name());
        }

        // finish dis
        return true;
    }

    private enum VisibilityHandler {

        NONE,
        GAME,
        SPECTATOR,
        OTHER

    }

}