package rip.thecraft.brawl.ability.abilities.legacy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

// TODO REWORK THIS ABILITY
public class ShadowShift {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
//            if (this.hasEquipped(player)) {
//                final double distance = 3;
//                final int duration = (5 * 20);
//                final int tier = 1;
//
//                final double angle = Math.random() * Math.PI * 2;
//                final double x = player.getLocation().getX() + Math.cos(angle) * distance;
//                final double z = player.getLocation().getZ() + Math.sin(angle) * distance;
//                final double y = player.getLocation().getY();
//                player.teleport(new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch()));
//
//                for (Entity entity : player.getNearbyEntities(distance, distance, distance)) {
//                    if (entity instanceof LivingEntity && Protection.canAttack(player, (LivingEntity) entity)) {
//                        ((LivingEntity) entity).addPotionEffect(
//                                new PotionEffect(PotionEffectType.BLINDNESS, duration, tier),
//                                true);
//                    }
//                }
//            }
        }
    }


}
