package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;

@AbilityData(icon = Material.FIREWORK, color = ChatColor.YELLOW)
public class Kangaroo extends Ability {

    @AbilityProperty(id = "jump-height")
    public float jumpHeight = 4F;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        player.setFallDistance(-(jumpHeight + 1));

        Vector vector = player.getEyeLocation().getDirection();
        vector.multiply(1.25F);
        vector.setY(jumpHeight / 4F);

        player.setVelocity(vector);

        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 10, 2);
    }
}
