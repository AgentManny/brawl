package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public interface InteractItemHandler extends AbilityHandler {

    /**
     * Represents an event that is called when a player interacts with an object or air,
     * potentially fired once for each hand. The hand can be determined using getHand().
     *
     * @param action Returns the action type
     * @param item Returns the item in hand represented by this event
     */
    boolean onInteractItem(Player player, Action action, ItemStack item);

}
