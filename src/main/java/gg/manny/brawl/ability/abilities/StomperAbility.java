package gg.manny.brawl.ability.abilities;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

@RequiredArgsConstructor
public class StomperAbility implements Ability {

    private final Brawl plugin;

    private double multiplier = .4;

    @Override
    public String getName() {
        return "Stomper";
    }

    @Override
    public void onActivate(Player player) {
//        if (this.hasCooldown(player)) return;

        Vector vector = player.getLocation().toVector();
        vector.setY(multiplier);
        player.setVelocity(vector);
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 0.0F);

        new BukkitRunnable() {

            boolean sneaked = false;

            @Override
            public void run() {
                if (player.isOnGround()) {
                    cancel();
                    plugin.getPlayerDataHandler().getPlayerData(player).addCooldown(getKey(), getCooldown());
                    ParticleEffect.EXPLOSION_HUGE.send(plugin.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
                    player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 0.0F);
                    return;
                }

                if (!sneaked && player.isSneaking()) {
                    sneaked = true; //prevent spam sneak.
                    Vector vector = player.getLocation().toVector();
                    vector.setY(-multiplier);
                    player.setVelocity(vector);
                    player.playSound(player.getLocation(), Sound.BAT_LOOP, 1.0F, 0.0F);
                    ParticleEffect.CLOUD.send(plugin.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
                    return;
                }

                ParticleEffect.SMOKE_NORMAL.send(plugin.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    @Override
    public void onDeactivate(Player player) {

    }
}
