package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityTask;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BatBlaster extends Ability {

    public double power = 0.7;
    public double yBase = 0.72;

    private final long duration = TimeUnit.SECONDS.toMillis(2);

    @Override
    public String getName() {
        return "Bat Blaster";
    }

    @Override
    public Material getType() {
        return Material.IRON_BARDING;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new BatBlastTask(player).start();
    }


    @Override
    public String getDescription() {
        return "Releases a swarm of bats in the pointed direction.";
    }

    @Override
    public int getDefaultCooldown() {
        return 15;
    }

    private class BatBlastTask extends AbilityTask {

        private final Location location;
        private final List<Bat> bats = new ArrayList<>();

        public BatBlastTask(Player player) {
            super(player, duration, 1L);

            for (int i = 0; i < 16; i++) {
                Bat bat = player.getWorld().spawn(player.getEyeLocation(), Bat.class);
                bats.add(bat);
            }

            location = player.getEyeLocation().clone();

        }

        @Override
        public void onTick() {
            for (Bat bat : bats) {
                Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D);
                bat.setVelocity(location.getDirection().clone().multiply(0.5D).add(rand));

                for (Player other : BrawlUtil.getNearbyPlayers(bat, 1)) {
                    if (!other.equals(player)) {
                        if (PlayerUtil.hit(bat, other)) {
                            other.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 3));

                            ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, bat.getLocation(), 12);

                            Vector unitVector = other.getLocation().toVector().subtract(bat.getLocation().toVector()).normalize();
                            other.setVelocity(unitVector.multiply(power).setY(yBase));

                            bat.getWorld().playSound(bat.getLocation(), Sound.BAT_HURT, 1.0F, 1.0F);
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
