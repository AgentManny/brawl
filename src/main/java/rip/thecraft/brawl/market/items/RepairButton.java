package rip.thecraft.brawl.market.items;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.player.PlayerData;

public class RepairButton extends MarketItem {

    public RepairButton() {
        super("Repair", Material.ANVIL, 200);
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Repairs your inventory and sword";
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

    }
}