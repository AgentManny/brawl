package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface AbilityProjectileLaunchHandler extends AbilityHandler {

    /**
     * Triggered when a projectile is launched
     *
     * @param player Attacked
     * @param projectile Projectile being spawned
     *
     * @return Returns whether event should be cancelled
     */
    boolean onProjectileLaunch(Player player, EntityType projectile);

}
