package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.Player;

public interface AbilityKillHandler extends AbilityHandler {

    /**
     * Called when killing a player
     * @param killer Player killing
     * @param victim Player being killed
     */
    void onKill(Player killer, Player victim);

}
