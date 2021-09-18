package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
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

        player.getActivePotionEffects().stream().filter(effect -> effect.getType().equals(PotionEffectType.SPEED))
                .findAny()
                .ifPresent(effect -> player.setMetadata("LastEffect", new FixedMetadataValue(Brawl.getInstance(), effect)));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 10), true);
    }

    @EventHandler
    public void onEffectExpire(PotionEffectExpireEvent event) {
        if (event.getEntity().hasMetadata("LastEffect")) {
            event.getEntity().addPotionEffect((PotionEffect) event.getEntity().getMetadata("LastEffect").get(0));
        }
    }

    @Override
    public double getDefaultCooldown() {
        return 10;
    }
}
