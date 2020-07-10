package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;

import java.util.HashSet;
import java.util.List;

public class Smite extends Ability implements Listener {

    private int smiteRadius = 15;

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public Material getType() {
        return Material.WOOD_AXE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, false)) return;
        this.addCooldown(player);

        List<Block> blocks = player.getLastTwoTargetBlocks(new HashSet<Material>(), smiteRadius);
        if (!blocks.isEmpty() && blocks.size() > 1) {
            Block targetBlock = blocks.get(1);
            Location location = player.getWorld().getHighestBlockAt(targetBlock.getLocation()).getLocation().clone()
                    .add(0, 1, 0);
            for (int i = 0; i < 3; i++) {
                LightningStrike strike = player.getWorld().strikeLightning(location);
            }
            this.addCooldown(player);

        } else {
            player.sendMessage(ChatColor.RED + "You can't smite here!");

        }
    }

    @Override
    public void onApply(Player player) {
        player.setMetadata("Smite", new FixedMetadataValue(Brawl.getInstance(), null));
    }

    @Override
    public void onDeactivate(Player player) {
        player.removeMetadata("Smite", Brawl.getInstance());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            if (event.getEntity().hasMetadata("Smite")) {
                event.setCancelled(true);
            }
        }
    }

}
