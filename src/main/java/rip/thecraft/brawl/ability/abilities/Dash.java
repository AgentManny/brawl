package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;

@AbilityData(
        name = "Dash",
        description = "Gain a short burst of speed.",
        icon = Material.SUGAR,
        color = ChatColor.DARK_AQUA
)
public class Dash extends Ability implements Listener {

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        player.getActivePotionEffects().stream().filter(effect -> effect.getType().equals(PotionEffectType.SPEED))
                .findAny()
                .ifPresent(effect -> player.setMetadata("LastEffect", new FixedMetadataValue(Brawl.getInstance(), effect)));

        Brawl.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.SPEED, 30, 10));
    }

}
