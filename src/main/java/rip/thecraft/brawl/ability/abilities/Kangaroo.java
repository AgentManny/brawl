package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;

public class Kangaroo extends Ability {

    public float jumpHeight = 4F;

    @Override
    public Material getType() {
        return Material.FIREWORK;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        addCooldown(player);

        player.setFallDistance(-(jumpHeight + 1));

        Vector vector = player.getEyeLocation().getDirection();
        vector.multiply(0.6F);
        vector.setY(jumpHeight / 4F);

        player.setVelocity(vector);
    }
}
