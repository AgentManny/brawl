package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public interface ProjectileLaunchHandler extends AbilityHandler {

    /**
     * Triggered when a projectile is launched
     *
     * @param player Attacked
     * @param projectile Projectile being spawned
     *
     * @return Returns whether event should be cancelled
     */
    boolean onProjectileLaunch(Player player, Projectile projectile);

}
