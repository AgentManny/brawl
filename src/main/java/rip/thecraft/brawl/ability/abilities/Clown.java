package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.BlockProjectileHitHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.moreprojectiles.event.BlockProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.event.CustomProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.projectile.BlockProjectile;

/**
 * Created by Flatfile on 10/19/2021.
 */
@AbilityData(
        name = "Clown Cake",
        description = "Throw a cake at your enemies to blind & disorient them.",
        icon = Material.CAKE,
        color = ChatColor.GOLD
)
public class Clown extends Ability implements BlockProjectileHitHandler {

    @AbilityProperty(id = "damage-value", description = "Amount of damage to deal to player")
    public double damageValue = 4.0D;

    @AbilityProperty(id = "throw-power", description = "Power of throwing the cake")
    public double throwPower = 1.2D;

    private boolean canAttack(Player player){
        return !RegionType.SAFEZONE.appliesTo(player.getLocation());
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new BlockProjectile("clownCake", player, Material.CAKE_BLOCK.getId(), 0, (float) throwPower);
        player.playSound(player.getLocation(), Sound.BURP, 1f, 1f);
    }

    @Override
    public boolean onBlockProjectileHit(Player shooter, Player hit, BlockProjectileHitEvent event) {
        if(event.getHitType() == CustomProjectileHitEvent.HitType.ENTITY && event.getProjectile().getProjectileName().equals("clownCake")){
            if(!canAttack(hit)) return true;
            if(hit == shooter) return true;

            if(!hit.isDead()){
                hit.damage(damageValue, shooter);
                hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4*20, 1, true, false));
                hit.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 8*20, 1, true, false));
                hit.playSound(hit.getLocation(), Sound.BLAZE_HIT, 2f, 2f);
            }
        }
        return false;
    }
}
