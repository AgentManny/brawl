package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Fireball extends Ability {

    @Override
    public void onInteractItem(Player player, Action action, ItemStack item) {
        if (item.getType() == Material.FIREBALL) {
            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.getInventory().remove(player.getItemInHand());
            }
            player.launchProjectile(org.bukkit.entity.Fireball.class);
        }
    }

    @Override
    public void onKill(Player player) {
        ItemStack fireball = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.isSimilar(new ItemStack(Material.FIREBALL))) {
                fireball = item;
                item.setAmount(item.getAmount() + 2);
            }
        }

        if (fireball == null) {
            player.getInventory().setItem(8, new ItemStack(Material.FIREWORK, 2));
        }
        player.updateInventory();
    }
}
