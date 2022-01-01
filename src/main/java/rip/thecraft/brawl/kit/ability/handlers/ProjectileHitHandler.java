package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface ProjectileHitHandler extends AbilityHandler {

    /**
     * Triggered when a projectile hits a player
     *
     * @param shooter Player shooting the projectile
     * @param victim Player hit by projectile
     *
     * @return Returns whether event should be cancelled
     */
    boolean onProjectileHit(Player shooter, Player victim, EntityDamageByEntityEvent event);
}
