package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;

import java.util.ArrayList;
import java.util.List;

public class Hellhound extends Ability {

    @Override
    public Material getType() {
        return Material.BONE;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        List<Wolf> hounds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Wolf hound = player.getWorld().spawn(player.getLocation(), Wolf.class);
            hound.setOwner(player);
            hound.setAngry(true);
            hound.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
            hound.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true, false));
            hound.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false));
            hound.playEffect(EntityEffect.VILLAGER_ANGRY);
            hounds.add(hound);
        }

        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {

            for (Wolf hound : hounds) {
                if (hound.isValid()) {
                    hound.playEffect(EntityEffect.WOLF_SMOKE);
                    hound.remove();
                }
            }

        }, 20L * 10);
    }
}
