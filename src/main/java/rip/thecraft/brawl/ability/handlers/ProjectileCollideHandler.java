package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public interface ProjectileCollideHandler extends AbilityHandler {

    /**
     * Called when an projectile collides with an entity
     *
     * @param shooter Player shooting the projectile
     * @param victim Get the entity the projectile collided with
     * @param projectile Projectile being spawned
     *
     * @return Returns whether event should be cancelled
     */
    boolean onProjectileCollide(Player shooter, Entity victim, Projectile projectile);

}
