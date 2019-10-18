package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.player.protection.Protection;
import gg.manny.brawl.util.ParticleEffect;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class Vampire extends Ability {

    private final Brawl plugin;

    @Override
    public Material getType() {
        return Material.MONSTER_EGG;
    }

    @Override
    public byte getData() {
        return (byte) 65;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_GRAY;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        List<Entity> bats = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            bats.add(player.getWorld().spawn(player.getEyeLocation(), Bat.class));
        }
        new BukkitRunnable() {

            long timestamp = System.currentTimeMillis();
            List<UUID> catched = new ArrayList<>();

            @Override
            public void run() {
                if (System.currentTimeMillis() - timestamp > 4500L || player == null) {
                    this.cancel();
                    return;
                }

                bats.stream().filter(Entity::isValid).forEachOrdered(bat -> {
                    Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D,
                            (Math.random() - 0.5D) / 3.0D);
                    if (bat != null && !bat.isDead()) {
                        bat.setVelocity(player.getLocation().getDirection().clone().multiply(0.5D).add(rand));
                    }

                    bat.getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player)
                            .map(entity -> (Player)entity)
                            .filter(other -> !other.equals(player) && !catched.contains(other.getUniqueId()) && !Protection.isAlly(player, other) && hitPlayer(bat.getLocation(), other)).forEachOrdered(other -> {

                        Vector v = bat.getLocation().getDirection();
                        v.normalize();
                        v.multiply(.4d);
                        v.setY(v.getY() + 0.2d);

                        if (v.getY() > 7.5) {
                            v.setY(7.5);
                        }

                        if (other.isOnGround()) {
                            v.setY(v.getY() + 0.4d);
                        }

                        other.setFallDistance(0);

                        if (Brawl.RANDOM.nextBoolean()) {
                            catched.add(other.getUniqueId());
                        }
                        bat.setPassenger(other);

                        ParticleEffect.send(Effect.SMALL_SMOKE, bat.getLocation(), 1);
                    });

                    for (Entity b : bats) {
                        if (b.getPassenger() != null && b.getPassenger() instanceof Player) {
                            ((Player) b.getPassenger()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 2));
                            ((Player) b.getPassenger()).playSound(b.getPassenger().getLocation(), Sound.BAT_TAKEOFF, 1.25f, 1.25f);
                        }
                    }
                });
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                for (Entity bat : bats) {
                    if (bat != null && !bat.isDead()) {
                        bat.remove();
                    }
                }
                catched.clear();
                super.cancel();

            }
        }.runTaskTimer(plugin, 4L, 4L);
    }

    private boolean hitPlayer(Location location, Player player) {
        Vector locVec = location.add(0, -location.getY(), 0).toVector();
        Vector playerVec = player.getLocation().add(0, -player.getLocation().getY(), 0).toVector();
        double vecLength = locVec.subtract(playerVec).length();


        if (vecLength < 0.8D) return true;

        if (vecLength < 1.2) {
            if ((location.getY() > player.getLocation().getY()) && (location.getY() < player.getEyeLocation().getY())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onKill(Player player) {

    }
}
