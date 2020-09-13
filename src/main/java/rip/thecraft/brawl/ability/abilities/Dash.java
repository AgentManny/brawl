package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;

public class Dash extends Ability implements Listener {

    @Override
    public Material getType() {
        return Material.SUGAR;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);


        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 10), true);
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }
}
