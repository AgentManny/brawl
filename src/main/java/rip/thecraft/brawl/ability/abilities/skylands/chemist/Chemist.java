package rip.thecraft.brawl.ability.abilities.skylands.chemist;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.AbilityKillHandler;
import rip.thecraft.brawl.ability.property.AbilityData;

@AbilityData
public class Chemist extends Ability implements AbilityKillHandler {

    @Override
    public void onKill(Player killer, Player victim) {
        ItemStack harming = null;
        ItemStack poison = null;
        if (killer != null) {
            for (ItemStack item : killer.getInventory().getContents()) {
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

            if (harming == null) {
                killer.getInventory().setItem(2, new ItemStack(Material.POTION, 2, (short) 16428));
            }
            if (poison == null) {
                killer.getInventory().setItem(3, new ItemStack(Material.POTION, 1, (short) 16420));
            }
            killer.updateInventory();
        }
    }
}