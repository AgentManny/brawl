package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;

/**
 * Created by Flatfile on 10/31/2021.
 */
public interface ToggleFlightHandler extends AbilityHandler {

    /**
     * Called when a player toggles their flying state
     * @param player Player sneaking
     * @param flying State to change
     */
    void onFlight(Player player, boolean flying);

}
