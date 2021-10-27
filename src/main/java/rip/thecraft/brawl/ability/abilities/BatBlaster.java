package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.task.AbilityTask;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AbilityData(
        name = "Bat Blaster",
        description = "Releases a swarm of bats in the pointed direction.",
        color = ChatColor.BLUE,
        icon = Material.IRON_BARDING
)
public class BatBlaster extends Ability {

    @AbilityProperty(id = "duration", description = "Duration in millis")
    public long duration = TimeUnit.SECONDS.toMillis(2);

    @AbilityProperty(id = "capture-player", description = "Should bats capture their victims")
    public boolean capturePlayer = true;

    @AbilityProperty(id = "power")
    public double power = 0.7;

    @AbilityProperty(id = "vertical", description = "Speed of bats rising upwards")
    public double vertical = 0.72;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new BatBlastTask(this, player).start();
    }

    private class BatBlastTask extends AbilityTask {

        private final Location location;
        private final List<Bat> bats = new ArrayList<>();

        public BatBlastTask(Ability ability, Player player) {
            super(ability, player, duration, 1L);

            for (int i = 0; i < 16; i++) {
                Bat bat = player.getWorld().spawn(player.getEyeLocation(), Bat.class);
                bats.add(bat);
            }

            location = player.getEyeLocation().clone();
        }

        @Override
        public void onTick() {
            for (Bat bat : bats) {
                if (bat.isDead()) continue;
                Location location = bat.getLocation();
                Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D);
                bat.setVelocity(this.location.getDirection().clone().multiply(0.5D).add(rand));
                if (RegionType.SAFEZONE.appliesTo(location)) {
                    bat.remove();
                    continue;
                }

                if (capturePlayer && bat.getPassenger() != null && bat.getPassenger() instanceof Player) {
                    Player victim = (Player) bat.getPassenger();
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 50, 3));
                    continue;
                }

                for (Player other : BrawlUtil.getNearbyPlayers(bat, 1)) {
                    if (!other.equals(player)) {
                        if (PlayerUtil.hit(bat, other)) {
                            if (!capturePlayer) {
                                other.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 50, 3));
                                Vector unitVector = bat.getVelocity().normalize();
                                other.setVelocity(unitVector
                                        .multiply(power)
                                        .setY(vertical)
                                );
                            } else {
                                if (inRange(bat.getLocation(), player)) { // Don't make it trigger all the time but also able to catch them
                                    capturePlayer(bat, other);
                                }
                            }

                            ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, bat.getLocation(), 12);
                            // other.playSound(location, Sound.BAT_HURT, 1.0F, 1.0F);
                            // bat.remove(); // Bat gets removed after it tags a player
                        }
                    }
                }
            }
        }

        @Override
        public void onCancel() {
            for (Bat bat : bats) {
                bat.remove();
            }
        }
    }

    private void capturePlayer(Entity bat, Player victim) {
        Vector v = bat.getLocation().getDirection();
        v.normalize();
        v.multiply(.4d);
        v.setY(v.getY() + 0.2d);

        if (v.getY() > 7.5) {
            v.setY(7.5);
        }

        if (victim.isOnGround()) {
            v.setY(v.getY() + 0.4d);
        }

        victim.setFallDistance(0);
        bat.setPassenger(victim);

        victim.playEffect(EntityEffect.WITCH_MAGIC);
//        victim.playSound(victim.getLocation(), captureSound, 1.25f, 1.25f);

        ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 1.5f, 1, bat.getLocation(), EFFECT_DISTANCE);
    }

    private boolean inRange(Location location, Player player) {
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
}
