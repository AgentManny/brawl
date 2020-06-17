package rip.thecraft.brawl.listener;

import rip.thecraft.brawl.region.selection.Selection;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        Selection selection = Selection.createOrGetSelection(player);

        Block clicked = event.getClickedBlock();
        int location = 0;

        if (item != null && item.getType() == Selection.SELECTION_WAND.getType() && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Selection Wand")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                location = 2;
                selection.setPoint2(clicked.getLocation());

            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                location = 1;
                selection.setPoint1(clicked.getLocation());

            } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
                location = 1;
                clicked = player.getEyeLocation().add(player.getLocation().getDirection()).getBlock();

            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                location = 2;
                clicked = player.getEyeLocation().add(player.getLocation().getDirection()).getBlock();
            }


            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);


            String message = ChatColor.YELLOW + (location == 1 ? "First" : "Second") +
                    " location " + ChatColor.YELLOW + "(" + ChatColor.GREEN +
                    clicked.getX() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                    clicked.getY() + ChatColor.YELLOW + ", " + ChatColor.GREEN +
                    clicked.getZ() + ChatColor.YELLOW + ")" + ChatColor.YELLOW + " has been set!";

            if (selection.isFullObject()) {
                message += ChatColor.GRAY + " (" + selection.getCuboid().volume()
                        + " blocks" + ")";
            }

            player.sendMessage(message);
        }
    }
}