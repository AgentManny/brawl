package rip.thecraft.brawl.market.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.player.PlayerData;

import java.util.concurrent.TimeUnit;

public class InventoryFillButton extends MarketItem {

    public InventoryFillButton() {
        super("Refill", Material.MUSHROOM_SOUP, 150);
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Fills your inventory with either soups or potions";
    }

    @Override
    public String getCooldown() {
        return "REFILL";
    }

    @Override
    public long getCooldownTime() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    public boolean requiresInventorySpace() {
        return true;
    }

    @Override
    public void purchase(Player player, PlayerData data) {
        if(player.getInventory().firstEmpty() == -1){
            player.sendMessage(ChatColor.RED + "Your inventory is full.");
            return;
        }

        ItemStack item = data.getRefillType().getItem();
        if (item.getType() != Material.AIR) {
            while (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            }
        }

        player.updateInventory();
    }
}