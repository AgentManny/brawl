package rip.thecraft.brawl.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
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

import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    private static EntityHider entityHider = Brawl.getInstance().getEntityHider();

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
            }
        }
    }

    private static boolean shouldSeePlayer(Player observer, Player target) {
        UUID observerId = observer.getUniqueId();
        UUID targetId = target.getUniqueId();

        DuelArenaHandler arenaHandler = Brawl.getInstance().getMatchHandler();
        GameHandler gameHandler = Brawl.getInstance().getGameHandler();

        Match targetMatch = arenaHandler.containsPlayer(observer, true);

        GameLobby lobby = gameHandler.getLobby();
        Game game = gameHandler.getActiveGame();

        if (game != null && game.containsPlayer(observer) && game.containsPlayer(target) && game.getState() != GameState.ENDED) {
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

        // finish dis
        return true;
    }

}