package rip.thecraft.brawl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.games.Thimble;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.game.type.BracketsGame;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game == null) return;

            GamePlayer gamePlayer = game.getGamePlayer(player);
            if (gamePlayer != null) {
                if (gamePlayer.isAlive()) {
                    if (!game.getFlags().contains(GameFlag.ALLOW_DAMAGE_GRACE) && game.getState() == GameState.GRACE_PERIOD) {
                        event.setCancelled(true);
                        return;
                    }

                    if (game instanceof BracketsGame) {
                        if (!((BracketsGame) game).contains(gamePlayer)) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (game.getFlags().contains(GameFlag.NO_PVP)) {
                        event.setCancelled(true);
                    } else if (game.getFlags().contains(GameFlag.NO_DAMAGE)) {
                        event.setDamage(0);
                    }
                }

            }
        }
    }

}
