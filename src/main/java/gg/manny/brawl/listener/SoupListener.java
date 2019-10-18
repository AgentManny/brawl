package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.server.util.chatcolor.CC;
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
                if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_PURPLE + "[Soup]")) {
                    Inventory inventory = Bukkit.createInventory(null, 27, "Soups");
                    PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
                    Kit selectedKit = playerData.getSelectedKit();
                    for(int i = 0; i < inventory.getSize(); i++) {
                        inventory.setItem(i, selectedKit == null ? new ItemStack(Material.MUSHROOM_SOUP) : selectedKit.getRefillType().getItem());
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
                PivotUtil.runLater(() -> event.getItemDrop().remove(), 5L, false);
                break;
            default: {
                event.setCancelled(true);
            }
        }
    }

}
