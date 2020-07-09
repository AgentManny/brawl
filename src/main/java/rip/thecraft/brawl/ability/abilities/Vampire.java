package rip.thecraft.brawl.ability.abilities;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class Vampire extends Ability {

    private double captureRange = 5;

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

            @Override
            public void run() {
                if (System.currentTimeMillis() - timestamp > 5000L || player == null) {
                    this.cancel();
                    return;
                }

                for (Entity bat : bats) {
                    if (bat.isValid() && !bat.isDead()) {
                        Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D,
                                (Math.random() - 0.5D) / 3.0D); // Makes the bats move randomly
                        Vector directionVector = player.getLocation().getDirection()
                                .clone()
                                .multiply(0.5D)
                                .add(rand);

                        bat.setVelocity(directionVector);

                        if (bat.getPassenger() != null && bat.getPassenger() instanceof Player) {
                            Player victim = (Player) bat.getPassenger();
                            victim.playEffect(EntityEffect.WITCH_MAGIC);

                            victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 2));
                            victim.playSound(victim.getLocation(), Sound.BAT_IDLE, 1.25f, 1.25f);
                        } else {
                            List<Player> victims = PlayerUtil.getNearbyPlayers(player, captureRange);
                            for (Player victim : victims) {
                                PlayerData victimData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(victim);
                                if (victimData.isSpawnProtection() || Protection.isAlly(player, victim)) continue;

                                if (inRange(bat.getLocation(), victim)) {
                                    if (Brawl.RANDOM.nextBoolean() && Brawl.RANDOM.nextBoolean() && Brawl.RANDOM.nextBoolean()) { // Don't make it trigger all the time but also able to catch them
                                        capturePlayer(bat, victim);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                for (Entity bat : bats) {
                    if (bat.isValid() && !bat.isDead()) {
                        bat.remove();
                    }
                }
                super.cancel();

            }
        }.runTaskTimer(Brawl.getInstance(), 10L, 10L);
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

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("capture-range", captureRange);
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        object.addProperty("capture-range", captureRange);
    }
}
