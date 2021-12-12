package rip.thecraft.brawl.ability.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.InteractItemHandler;
import rip.thecraft.brawl.ability.handlers.KillHandler;
import rip.thecraft.brawl.ability.property.AbilityData;

@AbilityData(
        name = "Fireball",
        icon = Material.FIREBALL,
        displayIcon = false
)
public class Fireball extends Ability implements KillHandler, InteractItemHandler {

    @Override
    public boolean onInteractItem(Player player, Action action, ItemStack item) {
        if (item.getType() == Material.FIREBALL) {
            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.getInventory().remove(player.getItemInHand());
            }
            player.launchProjectile(org.bukkit.entity.Fireball.class);
            return true;
        }

        return false;
    }

    @Override
    public void onKill(Player player, Player victim) {
        ItemStack fireball = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.isSimilar(new ItemStack(Material.FIREBALL))) {
                fireball = item;
                item.setAmount(item.getAmount() + 2);
            }
        }

        if (fireball == null) {
            player.getInventory().setItem(8, new ItemStack(Material.FIREBALL, 2));
        }
        player.updateInventory();
    }
}
