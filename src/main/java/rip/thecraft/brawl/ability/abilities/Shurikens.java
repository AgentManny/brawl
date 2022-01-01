package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.InteractItemHandler;
import rip.thecraft.brawl.ability.handlers.ItemProjectileHitHandler;
import rip.thecraft.brawl.ability.handlers.KillHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.moreprojectiles.event.CustomProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.event.ItemProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.projectile.ItemProjectile;
import gg.manny.streamline.util.ItemBuilder;

import java.util.concurrent.TimeUnit;

@AbilityData(
        name = "Shurikens",
        description = "Throw a shuriken that deals damage and blindness to your enemies.",
        color = ChatColor.DARK_PURPLE,
        icon = Material.NETHER_STAR,
        displayIcon = false
)
public class Shurikens extends Ability implements KillHandler, InteractItemHandler, ItemProjectileHitHandler {

    @AbilityProperty(id = "damage-value", description = "Amount of damage to deal to player")
    public double damageValue = 8.0D;

    @AbilityProperty(id = "throw-power", description = "Power of throwing the shuriken")
    public double throwPower = 1.2D;

    @AbilityProperty(id = "miss-cooldown")
    public int missCooldown = 10;

    @Override
    public boolean bypassAbilityPreventZone() {
        return true;
    }

    @Override
    public boolean onInteractItem(Player player, Action action, ItemStack itemStack) {
        if (itemStack.getType() == Material.NETHER_STAR) {
            if (this.hasCooldown(player, true)) return true;
            this.addCooldown(player, TimeUnit.SECONDS.toMillis(missCooldown));

            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.getInventory().remove(player.getItemInHand());
            }

            new ItemProjectile("shurikens", player, new ItemBuilder(Material.NETHER_STAR).build(), (float) throwPower);
            player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1f, 1.4f);
            return true;
        }

        return false;
    }

    private boolean canAttack(Player player){
        return !RegionType.SAFEZONE.appliesTo(player.getLocation());
    }

    @Override
    public boolean onItemProjectileHit(Player shooter, Player hit, ItemProjectileHitEvent event) {
        if(event.getHitType() == CustomProjectileHitEvent.HitType.ENTITY && event.getProjectile().getProjectileName().equals("shurikens")) {
            if (!canAttack(hit)) return true;
            if (hit == shooter) return true;

            if (!hit.isDead()) {
                hit.damage(damageValue, shooter);
                hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, true, false));
                hit.playEffect(hit.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 1);
                addCooldown(shooter);
            }
        }
        return false;
    }

    @Override
    public void onKill(Player player, Player victim) {
        ItemStack fireball = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.isSimilar(new ItemStack(Material.NETHER_STAR))) {
                fireball = item;
                item.setAmount(item.getAmount() + 3);
            }
        }

        if (fireball == null) {
            player.getInventory().setItem(1, new ItemStack(Material.NETHER_STAR, 3));
        }
        player.updateInventory();
    }
}