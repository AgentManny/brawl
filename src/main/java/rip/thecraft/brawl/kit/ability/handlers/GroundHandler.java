package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;

public interface GroundHandler extends AbilityHandler {

    /**
     * Called when a player lands on the ground
     * @param player Player landing
     * @param onGround Returns true of the player is on the ground
     */
    void onGround(Player player, boolean onGround);

}
