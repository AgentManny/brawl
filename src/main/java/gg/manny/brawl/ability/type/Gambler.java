package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.PotionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Gambler extends Ability {

    public Material getType() {
        return Material.GLASS_BOTTLE;
    }

    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    public void onActivate(Player player) {
        if (hasCooldown(player, true)) {
            return;
        }
        addCooldown(player, TimeUnit.SECONDS.toMillis(20L));

        ThreadLocalRandom random = ThreadLocalRandom.current();

        PotionEffectType randomPotionEffect = PotionEffectType.values()[random.nextInt(0, PotionEffectType.values().length)];
        int duration = random.nextInt(100, 300);
        int amplifier = random.nextInt(0, 3);

        PotionEffect chosenEffect = new PotionEffect(randomPotionEffect, duration, amplifier);
        player.sendMessage(ChatColor.YELLOW + "You've taken a gamble and received " + PotionUtils.getFriendlyName(chosenEffect) + ChatColor.YELLOW + ".");
        player.addPotionEffect(chosenEffect);
    }

}
