package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.PlayerUtil;

@AbilityData(
        name = "Time Lock",
        description = "Freeze nearby enemies in time.",
        color = ChatColor.GOLD,
        icon = Material.WATCH
)
public class TimeLock extends Ability {

    @AbilityProperty(id = "radius", description = "Radius of where it should slow")
    public int radius = 10;

    @AbilityProperty(id = "duration-ticks", description = "Freeze time for X ticks (1s = 20 tick)")
    public int durationTicks = 100;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        int enemiesFrozen = 0;
        for (Player enemy : PlayerUtil.getNearbyPlayers(player, radius)) {
            if (!Protection.isAlly(player, enemy)) {
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationTicks, 100, false, true));
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, durationTicks, 240, false, true));
                enemiesFrozen++;
            }
        }
        player.sendMessage(ChatColor.YELLOW + "You've froze " + ChatColor.AQUA + enemiesFrozen + ChatColor.YELLOW + " nearby enemies.");
    }
}