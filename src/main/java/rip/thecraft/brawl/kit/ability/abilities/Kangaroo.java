package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;

@AbilityData(
        name = "Kangaroo",
        description = "Jump high and far like a kangaroo.",
        icon = Material.FIREWORK,
        color = ChatColor.YELLOW
)
public class Kangaroo extends Ability {

    @AbilityProperty(id = "jump-height")
    public double jumpHeight = 4;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        player.setFallDistance((float) -(jumpHeight + 1));

        Vector vector = player.getEyeLocation().getDirection();
        vector.multiply(1.25F);
        vector.setY(jumpHeight / 4F);

        player.setVelocity(vector);

        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 10, 2);
    }
}
