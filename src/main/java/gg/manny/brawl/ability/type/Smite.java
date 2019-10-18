package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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

        for (int i = 0; i < 3; i++) {
            player.getWorld().strikeLightning(player.getTargetBlock(null, 256).getLocation());
        }
    }
}
