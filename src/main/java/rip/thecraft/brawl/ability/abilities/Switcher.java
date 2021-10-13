package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.KillHandler;
import rip.thecraft.brawl.ability.handlers.ProjectileHitHandler;
import rip.thecraft.brawl.ability.handlers.ProjectileLaunchHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.region.RegionType;

import java.util.concurrent.TimeUnit;

@AbilityData(color = ChatColor.LIGHT_PURPLE)
public class Switcher extends Ability implements ProjectileLaunchHandler, ProjectileHitHandler, KillHandler {

    @Override
    public boolean onProjectileLaunch(Player player, EntityType entityType) {
        if (entityType == EntityType.SNOWBALL) {
            boolean cancellable = this.hasCooldown(player, true);
            if (!cancellable) {
                this.addCooldown(player, TimeUnit.SECONDS.toMillis(5));
            } else {
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                player.updateInventory();
            }
            return cancellable;
        }
        return false;
    }

    @Override
    public boolean onProjectileHit(Player shooter, Player victim, EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            if (this.hasCooldown(shooter, true)) return true;

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

            addCooldown(shooter);

            Location shooterLoc = shooter.getLocation().clone();
            Location victimLoc = victim.getLocation().clone();

//            shooterLoc.setYaw(victim.getLocation().getYaw());
//            shooterLoc.setPitch(victim.getLocation().getPitch());
//
//            victimLoc.setYaw(shooter.getLocation().getYaw());
//            victimLoc.setPitch(shooter.getLocation().getPitch());

            shooter.teleport(victimLoc);
            victim.teleport(shooterLoc);

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
