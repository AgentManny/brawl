package rip.thecraft.brawl.kit.ability.abilities.legacy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.ParticleEffect;

// TODO REWORK THIS ABILITY
@AbilityData(
        color = ChatColor.DARK_GRAY,
        icon = Material.MONSTER_EGG,
        data = 58
)
public class Vortex extends Ability {

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.MONSTER_EGG, 1, (short) 58));
        item.setVelocity(player.getEyeLocation().getDirection().multiply(1.25));
        item.setPickupDelay(Integer.MAX_VALUE);

        new BukkitRunnable() {

            long timestamp = System.currentTimeMillis();

            boolean grounded = false;

            @Override
            public void run() {
                if (System.currentTimeMillis() - timestamp > 5000L) {
                    this.cancel();
                    return;
                }

                if (item != null && !item.isDead()) {

                    if (!grounded && item.isOnGround()) {
                        grounded = true;
                    }

                    if (grounded) {
                        Location loc = item.getLocation();
                        createVortex(player, item, loc);
                    }

                } else {
                    this.cancel();
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (item != null) {
                    item.remove();
                }
                super.cancel();

            }
        }.runTaskTimer(Brawl.getInstance(), 4L, 4L);
    }

    private void createVortex(Player player, Item item, Location loc) {
        int strands = 6;
        int particles = 25;
        float radius = 5;
        float curve = 10;
        double rotation = Math.PI / 4;

        Location location = loc.clone();
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = Math.cos(angle) * ratio * radius;
                double z = Math.sin(angle) * ratio * radius;
                location.add(x, 0, z);

                if (Brawl.RANDOM.nextBoolean()) {
                    ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 1.5f, 1, location, EFFECT_DISTANCE);
                } else {
                    ParticleEffect.FLAME.display(0, 0, 0, 1.5f, 1, location, EFFECT_DISTANCE);

                }

                location.subtract(x, 0, z);
            }
        }

        for (Entity entity : item.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof Player) {
                if (entity == player) continue;
                Vector vector = item.getLocation().toVector().subtract(entity.getLocation().toVector());
                entity.setVelocity(vector);
                if (!Protection.isAlly(player, (Player) entity)) {
                    ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 75, 40));
                    ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 75, 3));
                }
            }
        }
    }
}