package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
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
                for (Player other : BrawlUtil.getNearbyPlayers(bat, 1)) {
                    if (!other.equals(player)) {
                        if (PlayerUtil.hit(bat, other)) {
                            other.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 50, 3));

                            Vector unitVector = other.getLocation().toVector().subtract(location.toVector()).normalize();
                            other.setVelocity(unitVector
                                    .multiply(power)
                                    .setY(vertical)
                            );

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
}
