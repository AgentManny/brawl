package rip.thecraft.brawl.ability.abilities;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.InteractItemHandler;
import rip.thecraft.brawl.ability.handlers.KillHandler;
import rip.thecraft.brawl.ability.handlers.ProjectileHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.ProjectileEffect;

import java.util.concurrent.TimeUnit;

@AbilityData(color = ChatColor.LIGHT_PURPLE)
public class Switcher extends Ability implements InteractItemHandler, ProjectileHandler, KillHandler {

    private static final String SWITCHER_META = "Switcher";

    @AbilityProperty(id = "miss-cooldown")
    public int missCooldown = 3;

    @Override
    public boolean onInteractItem(Player player, Action action, ItemStack item) {
        if (item.getType() == Material.SNOW_BALL) {
            if (hasCooldown(player, true)) return true;

            if (player.hasMetadata(SWITCHER_META)) {
                long projectileTimer = player.getMetadata(SWITCHER_META, Brawl.getInstance()).asLong();
                if (projectileTimer >= System.currentTimeMillis()) {
                    player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(projectileTimer - System.currentTimeMillis(), true, true) + ChatColor.RED + " before throwing this again.");
                    return true;
                }
            }
            player.setMetadata(SWITCHER_META, new FixedMetadataValue(Brawl.getInstance(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(missCooldown)));
        }
        return false;
    }

    @Override // Visuals
    public boolean onProjectileLaunch(Player player, Projectile projectile) {
        if (projectile instanceof Snowball) {
            // Interact sometimes ignores snowballs - Ensures they don't spawn
            if (player.hasMetadata(SWITCHER_META) && player.getMetadata(SWITCHER_META, Brawl.getInstance()).asLong() >= System.currentTimeMillis()) {
                return true;
            }
            new ProjectileEffect(projectile, ParticleEffect.SPELL_MOB)
                    .color(Color.WHITE)
                    .intervals(1)
                    .duration(3) // You should be switching within this time anyways
                    .start();
        }
        return false;
    }

    @Override
    public void onDeactivate(Player player) {
        player.removeMetadata(SWITCHER_META, Brawl.getInstance());
    }

    @Override
    public boolean onProjectileCollide(Player shooter, Entity entity, Projectile projectile) {
        if (projectile instanceof Snowball && entity instanceof Player) {
            Player victim = (Player) entity;
            if ((RegionType.SAFEZONE.appliesTo(shooter.getLocation()) || RegionType.SAFEZONE.appliesTo(victim.getLocation()))) {
                shooter.sendMessage(ChatColor.RED + "You cannot use abilities in spawn.");
                shooter.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                shooter.updateInventory();
                return true;
            }

            if (RegionType.NO_ABILITY_ZONE.appliesTo(shooter.getLocation()) || RegionType.NO_ABILITY_ZONE.appliesTo(victim.getLocation())) {
                shooter.sendMessage(ChatColor.RED + "You cannot use abilities in this area.");
                shooter.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                shooter.updateInventory();
                return true;
            }

            Location shooterLoc = shooter.getLocation().clone();
            Location victimLoc = victim.getLocation().clone();

//            shooterLoc.setYaw(victim.getLocation().getYaw());
//            shooterLoc.setPitch(victim.getLocation().getPitch());
//
//            victimLoc.setYaw(shooter.getLocation().getYaw());
//            victimLoc.setPitch(shooter.getLocation().getPitch());

            shooter.teleport(victimLoc);
            victim.teleport(shooterLoc);

            addCooldown(shooter);

            shooter.playSound(shooterLoc, Sound.CHICKEN_EGG_POP, .75f, 1);
            victim.playSound(victimLoc, Sound.CHICKEN_EGG_POP, .75f, 1);
        }
        return false;
    }

    @Override
    public void onKill(Player player, Player victim) {
        ItemStack switcher = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.isSimilar(new ItemStack(Material.SNOW_BALL))) {
                switcher = item;
                item.setAmount(item.getAmount() + 3);
            }
        }

        if (switcher == null) {
            player.getInventory().setItem(1, new ItemStack(Material.SNOW_BALL, 3));
        }
        player.updateInventory();
    }
}