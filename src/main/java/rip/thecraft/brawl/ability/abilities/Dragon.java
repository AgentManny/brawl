package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.task.AbilityTask;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

@AbilityData(
        name = "Fire Breathe",
        color = ChatColor.GOLD,
        icon = Material.FIREBALL
)
public class Dragon extends Ability implements Listener {

    @AbilityProperty(id = "duration", description = "Duration of the task")
    public long taskDuration = 10000; // Temp - To debug tasks

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

        new DragonTask(this, player).start();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 2);
    }

    private class DragonTask extends AbilityTask {

        double t;
        double o;
        Vector direction;
        Location location;

        protected DragonTask(Dragon dragon, Player player) {
            super(dragon, player, taskDuration, 1L);

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

            for (Player other : PlayerUtil.getNearbyPlayers(location, 1)) {
                if (!other.equals(player)) {
                    double distance = other.getLocation().distance(location);
                    if (distance < 2.90) {
                        other.damage(damagePerTick, player);
                        other.setFireTicks(fireTicks);
                    }

                }
            }
            location.subtract(x, y, z);
        }

        @Override
        public boolean shouldCancel() {
            boolean cancel = super.shouldCancel();
            if (RegionType.SAFEZONE.appliesTo(location)) {
                cancel = true;
            }
            return cancel;
        }

        @Override
        public void onCancel() { }
    }
}