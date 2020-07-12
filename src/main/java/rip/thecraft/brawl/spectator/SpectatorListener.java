package rip.thecraft.brawl.spectator;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.util.EntityHider;
import rip.thecraft.spartan.util.PlayerUtils;

@RequiredArgsConstructor
public class SpectatorListener implements Listener {

    private final EntityHider entityHider = Brawl.getInstance().getEntityHider();

    private final SpectatorManager spectatorManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        spectatorManager.spectators.values().forEach(spectator -> entityHider.hideEntity(event.getPlayer(), spectator.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        spectatorManager.removeSpectator(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (spectatorManager.isSpectating(player)) {
                event.setCancelled(true);
            }

            Player damager = PlayerUtils.getDamager(event);
            if (damager != null && spectatorManager.isSpectating(damager)) {
                event.setCancelled(true);
            }
        }
    }
}
