package rip.thecraft.brawl.ability.abilities.classic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.BukkitUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Gambler extends Ability {

    public Material getType() {
        return Material.POTION;
    }

    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public String getDescription() {
        return "Gives you random effects that can help you positively or negatively";
    }

    public void onActivate(Player player) {
        if (hasCooldown(player, true)) {
            return;
        }
        addCooldown(player, TimeUnit.SECONDS.toMillis(15L));

        ThreadLocalRandom random = ThreadLocalRandom.current();

        PotionEffectType randomPotionEffect = PotionEffectType.values()[random.nextInt(0, PotionEffectType.values().length - 1)];
        int duration = random.nextInt(80, 300);
        int amplifier = random.nextInt(0, 3);

        PotionEffect chosenEffect = new PotionEffect(randomPotionEffect, duration, amplifier);
        player.sendMessage(ChatColor.YELLOW + "You've taken a gamble and received " + BukkitUtil.getFriendlyName(chosenEffect) + ChatColor.YELLOW + ".");
        player.addPotionEffect(chosenEffect);
    }

}
