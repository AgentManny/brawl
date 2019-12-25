package gg.manny.brawl.game.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.game.GameState;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.brawl.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FFA extends Game implements Listener {

    private Kit defaultKit;

    public FFA() {
        super(GameType.FFA, GameFlag.PLAYER_ELIMINATE);
        defaultKit = Brawl.getInstance().getKitHandler().getDefaultKit();
    }

    @Override
    public void setup() {
        super.setup();
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            defaultKit.apply(player, false, true);
        });
    }

    public void handleElimination(Player player, Location location, boolean disconnected) {
        if (eliminate(player)) {
            broadcast(ChatColor.DARK_RED + player.getName() + ChatColor.RED + (disconnected ? " disconnected" : " has been eliminated") + ".");
            if (!disconnected) {
                player.teleport(location);
                Brawl.getInstance().getSpectatorManager().addSpectator(player, this);
                Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
                    player.teleport(location);

                }, 10L);
            }

            // Find a winner
            if (this.getAlivePlayers().size() == 1) {
                GamePlayer winner = this.getAlivePlayers().get(0);
                this.winners.add(winner);

                this.end();
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof FFA) {
                GamePlayer gamePlayer = this.getGamePlayer(player);
                if (gamePlayer != null) {
                    if (gamePlayer.isAlive()) {
                        if (this.state == GameState.GRACE_PERIOD) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
