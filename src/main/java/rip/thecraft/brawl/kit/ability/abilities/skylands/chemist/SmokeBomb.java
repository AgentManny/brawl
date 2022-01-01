package rip.thecraft.brawl.kit.ability.abilities.skylands.chemist;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.util.ParticleEffect;

import java.util.List;
import java.util.Set;

@AbilityData(
        name = "Smoke Bomb",
        description = "Throw a smoke bomb to confuse your enemies and escape easily.",
        icon = Material.FIREWORK_CHARGE,
        color = ChatColor.DARK_GRAY
)
public class SmokeBomb extends Ability implements Listener {

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        ItemStack bomb = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bomb.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 150, 3), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 150, 3), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 3), true);
        bomb.setItemMeta(meta);


        ThrownPotion thrownPotion = player.launchProjectile(ThrownPotion.class);
        thrownPotion.setItem(bomb);
        List<Block> lineOfSight = player.getLineOfSight((Set<Material>) null, 5);
        for (Block block : lineOfSight) {
            ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 1.5f, 5, block.getLocation(), EFFECT_DISTANCE);
        }
        thrownPotion.setMetadata("chemist", new FixedMetadataValue(Brawl.getInstance(), player.getUniqueId()));
    }

    @EventHandler
    public void onPotion(PotionSplashEvent event) {
        if (event.getPotion().hasMetadata("chemist")) {
            ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 1.5f, 10, event.getPotion().getLocation(), EFFECT_DISTANCE);
        }
    }
}
