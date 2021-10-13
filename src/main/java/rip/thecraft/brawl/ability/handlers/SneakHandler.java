package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.Player;

public interface SneakHandler extends AbilityHandler {

    /**
     * Called when a player toggles their sneaking state
     * @param player Player sneaking
     * @param sneaking State to change
     */
    void onSneak(Player player, boolean sneaking);

}
