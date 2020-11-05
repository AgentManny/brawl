package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.PlayerUtil;

public class TimeLock extends Ability {

    private double radius = 10.0;
    private int durationTicks = 50; // Freeze time for X ticks (1s = 20 tick)

    @Override
    public String getName() {
        return "Time Lock";
    }

    @Override
    public Material getType() {
        return Material.WATCH;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        addCooldown(player);

        int enemiesFrozen = 0;
        for (Player enemy : PlayerUtil.getNearbyPlayers(player, radius)) {
            if (!Protection.isAlly(player, enemy)) {
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationTicks, 100, false, true));
                enemiesFrozen++;
            }
        }
        player.sendMessage(ChatColor.YELLOW + "You've froze " + ChatColor.AQUA  + enemiesFrozen + ChatColor.YELLOW + " nearby enemies.");
    }
}
