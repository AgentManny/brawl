package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SoupListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onSign(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Soup]") && event.getPlayer().isOp()) {
            event.setLine(0, CC.DARK_PURPLE + "[Soup]");
            event.setLine(1, "Click here");
            event.setLine(2, "to refill");
            event.setLine(3, "your soups.");
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            if(playerData.getSelectedKit() != null) {
                event.setCancelled(true);
                event.setFoodLevel(20);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            double health = event.getPlayer().getHealth();
            if (event.hasItem() && event.getItem().getTypeId() == 282 && health < 20D) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.setHealth(health + 7 > 20D ? 20 : health + 7);
                player.getItemInHand().setType(Material.BOWL);
            } else if (event.hasItem() && event.getItem().getTypeId() == 282 && event.getPlayer().getFoodLevel() < 20) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.setFoodLevel((player.getFoodLevel() + 7) > 20D ? 20 : player.getFoodLevel() + 7);
                player.getItemInHand().setType(Material.BOWL);
            } else if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if(sign.getLine(0).equalsIgnoreCase(ChatColor.GOLD + "[Soup]")) {
                    Inventory inventory = Bukkit.createInventory(null, 27, "Soups");
                    for(int i = 0; i < inventory.getSize(); i++) {
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }
                    event.getPlayer().openInventory(inventory);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Material type = event.getItemDrop().getItemStack().getType();
        switch(type) {
            case MUSHROOM_SOUP:
            case BOWL:
            case GLASS_BOTTLE:
                event.getItemDrop().remove();
                break;
            default: {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Material type = event.getItem().getItemStack().getType();
        if (!(type == Material.MUSHROOM_SOUP ||
                player.getInventory().getHelmet().getType() == type || player.getInventory().getChestplate().getType() == type ||
                player.getInventory().getLeggings().getType() == type || player.getInventory().getBoots().getType() == type)) {

            event.setCancelled(true);
        }
    }
}
