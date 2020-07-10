package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.ability.Ability;

import java.util.HashSet;

public class Smite extends Ability implements Listener {

    @Override
    public ChatColor getColor() {
        return ChatColor.WHITE;
    }

    @Override
    public Material getType() {
        return Material.WOOD_AXE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, false)) return;
        this.addCooldown(player);

        for (int i = 0; i < 5; i++) {
            player.getWorld().spigot().strikeLightning(player.getTargetBlock(new HashSet<Material>(), 256).getLocation(), true);

        }
    }
}
