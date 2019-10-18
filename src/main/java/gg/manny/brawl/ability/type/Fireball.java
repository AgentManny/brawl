package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.region.RegionType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Fireball extends Ability implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.hasItem() && event.getItem() != null && hasEquipped(player) && event.getItem().getType() == Material.FIREBALL && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            if (event.getPlayer().getItemInHand().getAmount() > 1) {
                event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
            } else {
                event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                //event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            }
            player.updateInventory();
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
