package rip.thecraft.brawl.listener;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

@RequiredArgsConstructor
public class AbilityListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onProject(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                arrow.setMetadata("ShotFrom", new FixedMetadataValue(plugin, event.getEntity().getLocation()));
                arrow.setMetadata("Force", new FixedMetadataValue(plugin, event.getForce()));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Kit selectedKit = KitHandler.getEquipped(player);
        if (event.hasItem() && event.getItem() != null && selectedKit != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            boolean cancelled = false;
            for (Ability ability : selectedKit.getAbilities()) {
                if (!cancelled && ability.onInteractItem(player, event.getAction(), event.getItem())) {
                    cancelled = true; // Allow continue iteration but also cancel if found a match
                }
            }

            if (cancelled) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
                player.updateInventory(); // prevent stupid glitches
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            Kit selectedKit = KitHandler.getEquipped(player);
            if (selectedKit != null) {
                for (Ability ability : selectedKit.getAbilities()) {
                    if (ability.onProjectileLaunch(player, event.getEntityType())) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        Player shooter = null;
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                shooter = (Player) projectile.getShooter();
            }
        }
        if (shooter != null) {
            Kit selectedKit = KitHandler.getEquipped(shooter);
            if (selectedKit != null) {
                for (Ability ability : selectedKit.getAbilities()) {
                    if (ability.onProjectileHit(shooter, (Player) event.getEntity(), event)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

}
