package rip.thecraft.brawl.ability.abilities.skylands.chemist;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.KillHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;

@AbilityData(
        color = ChatColor.DARK_PURPLE,
        icon = Material.POTION,
        data = (byte) 16428,
        displayIcon = false
)
public class Chemist extends Ability implements KillHandler {

    @AbilityProperty(id = "limit", description = "Duration for maximum charge time")
    public int limit = 6;

    @Override
    public void onKill(Player killer, Player victim) {
        ItemStack harming = null;
        ItemStack poison = null;
        if (killer != null) {
            for (ItemStack item : killer.getInventory().getContents()) {
                if (item == null) continue;
                if (item.isSimilar(new ItemStack(Material.POTION, 1, (short) 16428))) {
                    int outcome = item.getAmount() + 2;
                    if(outcome > limit) return;

                    harming = item;
                    item.setAmount(item.getAmount() + 2);
                }
                if (item.isSimilar(new ItemStack(Material.POTION, 1, (short) 16420))) {
                    int outcome = item.getAmount() + 1;
                    if(outcome > limit) return;
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