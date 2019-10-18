package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.chatcolor.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Archer extends Ability {

    @Override
    public boolean onProjectileHit(Player shooter, Player victim, EntityDamageByEntityEvent event) {
        Entity damager = event.getEntity();
        if (damager instanceof Arrow && victim != shooter) {
            if (hasEquipped(victim)) {
                shooter.sendMessage(CC.RED + CC.BOLD + "Archer! " + CC.YELLOW + "Cannot damage other Archers.");
                return false;
            }

            float pullback = (float) damager.getMetadata("Force").get(0).value();

            Location from = (Location) damager.getMetadata("ShotFrom").get(0).value();
            Location to = victim.getLocation();
            double x = to.getX() - from.getX();
            double z = to.getZ() - from.getZ();
            double distance = Math.sqrt(x * x + z * z);
            double multiplier = 1;
            if (distance >= 16.0) {
                multiplier = Math.min(distance, 50.0) / 25;
            }

            if (shooter.getLocation().getY() > victim.getEyeLocation().getY()) {
                multiplier += 0.10;
                shooter.playSound(shooter.getLocation(), Sound.EXPLODE, 5F, 20F);
                shooter.sendMessage(CC.DARK_PURPLE + CC.BOLD + "Headshot! " + CC.YELLOW + "Inflicted damage increased by " + CC.LIGHT_PURPLE + "10%" + CC.YELLOW + ".");
            }

            if (pullback < 0.5f) {
                shooter.sendMessage(CC.RED + CC.BOLD + "Archer! " + CC.YELLOW + "Bow wasn't drawn back. Failed to launch");
                return true;
            }

            if (multiplier != 0) {
                event.setDamage(Math.min(event.getDamage() * multiplier, 15));
                double damage = event.getFinalDamage();
                shooter.sendMessage(CC.YELLOW + "Damaged " + CC.WHITE + victim.getDisplayName() + CC.YELLOW + " inflicting " + CC.RED + (((int) (damage / 2)) + " heart" + ((damage / 2 == 1) ? "" : "s")) + CC.YELLOW + " from " + CC.LIGHT_PURPLE + (int) Math.round(distance) + " blocks" + CC.YELLOW + " away.");

            }
        }

        return false;
    }

    @Override
    public void onKill(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) (armor.getDurability() - 10));
            }
        }
    }
}
