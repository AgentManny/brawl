package rip.thecraft.brawl.market.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.player.PlayerData;

public class GoldenAppleButton extends MarketItem {

    public GoldenAppleButton() {
        super("Golden Apple", Material.GOLDEN_APPLE, 75);
    }

    @Override
    public int getWeight() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void purchase(Player player, PlayerData data) {
        player.updateInventory();
        player.sendMessage(ChatColor.YELLOW + "You have purchased a " + ChatColor.GOLD + "Golden Apple" + ChatColor.YELLOW + " for " + ChatColor.LIGHT_PURPLE + credits + " credits" + ChatColor.YELLOW + ".");
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
    }
}