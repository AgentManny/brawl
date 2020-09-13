package rip.thecraft.brawl.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestListener implements Listener {

    private void onInventoryInteract(InventoryInteractEvent event) {
        Player actor = event.getActor();
        Inventory inventory = event.getInventory();

        boolean cancelled = false;
        if (inventory.getType() == InventoryType.ENDER_CHEST) {
            if (event instanceof InventoryDragEvent) {
                InventoryDragEvent dragEvent = (InventoryDragEvent) event;

            }
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                ItemStack currentItem = clickEvent.getCurrentItem();
                if (currentItem.getType() == Material.GOLD_INGOT) {
                    return;
                }
            }
            cancelled = true;
        }

        if (cancelled) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItem(InventoryClickEvent event) {
        onInventoryInteract(event);
    }


}
