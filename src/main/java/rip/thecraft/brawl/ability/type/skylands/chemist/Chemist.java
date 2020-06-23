package rip.thecraft.brawl.ability.type.skylands.chemist;

import rip.thecraft.brawl.ability.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Chemist extends Ability {

    @Override
    public void onKill(Player player) {
        ItemStack harming = null;
        ItemStack poison = null;
        if (player != null) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null) continue;
                if (item.isSimilar(new ItemStack(Material.POTION, 1, (short) 16428))) {
                    harming = item;
                    item.setAmount(item.getAmount() + 2);
                }
                if (item.isSimilar(new ItemStack(Material.POTION, 1, (short) 16420))) {
                    poison = item;
                    item.setAmount(item.getAmount() + 1);
                }
        }
        }
        if(harming == null) {
            player.getInventory().setItem(2, new ItemStack(Material.POTION, 2, (short)16428));
        }
        if(poison == null) {
            player.getInventory().setItem(3, new ItemStack(Material.POTION, 1, (short)16420));
        }
        player.updateInventory();
    }
}
