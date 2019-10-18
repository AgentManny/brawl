package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Switcher extends Ability {

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public boolean onProjectileLaunch(Player player, EntityType entityType) {
        boolean cancellable = this.hasCooldown(player, true);
        if (!cancellable) {
            this.addCooldown(player, 5);
        } else {
            player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
            player.updateInventory();
        }
        return cancellable;
    }

    @Override
    public boolean onProjectileHit(Player shooter, Player victim, EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            if (this.hasCooldown(shooter, true)) return true;
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
}
