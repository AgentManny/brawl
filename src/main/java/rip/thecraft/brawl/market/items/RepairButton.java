package rip.thecraft.brawl.market.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.player.PlayerData;

public class RepairButton extends MarketItem {

    public RepairButton() {
        super("Repair", Material.MUSHROOM_SOUP, 150);
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
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().getMaxDurability() > 0) {
                content.setDurability((short) 0);
            }
        }

        for (ItemStack content : player.getInventory().getArmorContents()) {
            if (content != null && content.getType().getMaxDurability() > 0) {
                content.setDurability((short) 0);
            }
        }

        player.sendMessage(ChatColor.GREEN + "You have repaired all items in your inventory.");
    }
}