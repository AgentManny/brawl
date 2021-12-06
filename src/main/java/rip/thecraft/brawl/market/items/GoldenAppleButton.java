package rip.thecraft.brawl.market.items;

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
    public boolean requiresInventorySpace() {
        return true;
    }

    @Override
    public void purchase(Player player, PlayerData data) {
        player.updateInventory();
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
    }
}