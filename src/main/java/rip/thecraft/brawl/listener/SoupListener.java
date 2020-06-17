package rip.thecraft.brawl.listener;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.SchedulerUtil;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.List;

@RequiredArgsConstructor
public class SoupListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onSign(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Refill]") && event.getPlayer().isOp()) {
            event.setLine(0, CC.DARK_PURPLE + "[Refill]");
            event.setLine(1, "Click here");
            event.setLine(2, "to refill");
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.getFlags().contains(GameFlag.HUNGER) && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) return;

            event.setCancelled(true);
            event.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Player player = event.getPlayer();

            double health = event.getPlayer().getHealth();

            double maxHealth = event.getPlayer().getMaxHealth();
            if (event.hasItem() && event.getItem().getTypeId() == 282 && health < maxHealth) {
                event.setCancelled(true);
                player.setHealth(health + 7 > maxHealth ? maxHealth : health + 7);
                player.getItemInHand().setType(Material.BOWL);
            } else if (event.hasItem() && event.getItem().getTypeId() == 282 && event.getPlayer().getFoodLevel() < 20) {
                event.setCancelled(true);
                player.setFoodLevel((player.getFoodLevel() + 7) > 20D ? 20 : player.getFoodLevel() + 7);
                player.getItemInHand().setType(Material.BOWL);
            } else if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_PURPLE + "[Refill]")) {
                    Inventory inventory = Bukkit.createInventory(null, 27, "Refill");
                    PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
                    ItemStack item = playerData.getRefillType().getItem();
                    for(int i = 0; i < inventory.getSize(); i++) {
                        inventory.setItem(i, item);
                    }
                    event.getPlayer().openInventory(inventory);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null) return;

        Material type = item.getType();
        switch(type) {
            case MUSHROOM_SOUP:
            case BOWL:
            case GLASS_BOTTLE:
                SchedulerUtil.runTaskLater(() -> event.getItemDrop().remove(), 5L, false);
                break;
            default: {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());
                    if (playerData.getSelectedKit() != null) {
                        List<String> lore = item.getItemMeta().getLore();
                        boolean canDrop = lore.contains(ChatColor.GRAY + "PvP Loot") || lore.contains(ChatColor.DARK_GRAY + playerData.getSelectedKit().getName());
                        if (canDrop) {
                            SchedulerUtil.runTaskLater(() -> event.getItemDrop().remove(), 5L, false);
                            return;
                        }
                    }
                }

                event.setCancelled(true);
            }
        }
    }
    //PlayerPickupItemEvent

}
