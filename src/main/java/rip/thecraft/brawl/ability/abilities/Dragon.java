package rip.thecraft.brawl.ability.abilities;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityTask;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

public class Dragon extends Ability implements Listener {

    public Dragon() {
        addProperty("duration", 2500, "Duration of the task");
        addProperty("distanceMultiplier", 1.5, "idk just play with it");
        addProperty("circularNumber", 1.25, "honestly no idea just play with it");
        addProperty("damagePerTick", 3, "Adjust the amount of damage a player is dealt per tick");
        addProperty("fireTicks", 5, "Adjust the amount of ticks the player is set on fire for");
    }

    long taskDuration = 2500;
    double distanceMultiplier = 1.5;
    double circularNumber = 1.25;
    double damagePerTick = 7;
    int fireTicks = 40;

    @Override
    public String getName() {
        return "Fire Breathe";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getType() {
        return Material.FIREBALL;
    }

    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

//        taskDuration = getProperty("duration").longValue();
//        distanceMultiplier = getProperty("distanceMultiplier");
//        circularNumber = getProperty("circularNumber");
//        damagePerTick = getProperty("damagePerTick");
//        fireTicks = getProperty("fireTicks").intValue();

        new DragonTask(player).start();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 2);
        addCooldown(player);
    }

    public class DragonTask extends AbilityTask {

        double t;
        double o;
        Vector direction;
        Location location;

        protected DragonTask(Player player) {
            super(player, taskDuration, 1L);

            direction = player.getLocation().clone().getDirection().normalize();
            location = player.getLocation().clone();
        }

        @Override
        public void onTick() {
            t += distanceMultiplier;
            o += circularNumber;
            double x = direction.getX() * t + Math.cos(o);
            double y = direction.getY() * t + 1.5 + Math.sin(o);
            double z = direction.getZ() * t + Math.sin(o);

            location.add(x, y, z);
            ParticleEffect.FLAME.display(0, 0, 0, 0, 1, location, 50);
            ParticleEffect.FLAME.display(1, 0, 1, 0, 1, location, 50);
            ParticleEffect.FLAME.display(-1, 0, -1, 0, 1, location, 50);
            for (Entity entity : location.getChunk().getEntities()) {
                double distance = entity.getLocation().distance(location);
                if (entity instanceof Player && !entity.equals(player) && distance < 2.90) {
                    ((Player) entity).damage(damagePerTick, player);
                    ((Player) entity).setFireTicks(fireTicks);
                }
            }

            location.subtract(x, y, z);
        }

        @Override
        public void onCancel() { }
    }


}
