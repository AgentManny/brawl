package rip.thecraft.brawl.market.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.player.PlayerData;

public class InventoryFillButton extends MarketItem {

    public InventoryFillButton() {
        super("Fill Inventory", Material.MUSHROOM_SOUP, 150);
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
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
    public void purchase(Player player, PlayerData playerData) {
        while (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
    }
}