package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityTask;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.ParticleEffect;

@AbilityData(
        name = "Fire Breathe",
        color = ChatColor.GOLD,
        icon = Material.FIREBALL
)
public class Dragon extends Ability implements Listener {

    @AbilityProperty(id = "duration", description = "Duration of the task")
    public long taskDuration = 2500;

    @AbilityProperty(id = "distance-multiplier", description = "Distance of flame")
    public double distanceMultiplier = 1.5;

    @AbilityProperty(id = "circular-number", description = "Intervals of flame directional change")
    public double circularNumber = 1.25;

    @AbilityProperty(id = "damage-per-tick", description = "Adjust amount of damage a player is dealt per tick")
    public double damagePerTick = 7;

    @AbilityProperty(id = "fire-ticks", description = "Adjust amount of ticks player is set on fire for")
    public int fireTicks = 40;

    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new DragonTask(player).start();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 2);
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