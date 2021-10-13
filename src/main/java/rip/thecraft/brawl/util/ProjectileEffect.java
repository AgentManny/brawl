package rip.thecraft.brawl.util;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ProjectileEffect {

    private static final int VISUAL_EFFECT_DISTANCE = 25;

    private final Projectile projectile;
    private final ParticleEffect trailingEffect;

    private boolean killAfterExpiry = false;
    private Consumer<Projectile> expiryFunction;

    private long duration = TimeUnit.SECONDS.toMillis(10); // In case projectile isn't dead (e.g. thrown in void)

    private ParticleEffect.OrdinaryColor color = null;

    private long intervalTicks = 2;

    /**
     * {@link ParticleEffect#REDSTONE},
     * {@link ParticleEffect#SPELL_MOB}, {@link ParticleEffect#SPELL_MOB_AMBIENT} and {@link ParticleEffect#NOTE}
     * @param color
     * @return
     */
    public ProjectileEffect color(Color color) {
        this.color = new ParticleEffect.OrdinaryColor(color);
        return this;
    }

    public ProjectileEffect intervals(long intervalTicks) {
        this.intervalTicks = intervalTicks;
        return this;
    }

    public ProjectileEffect duration(int duration) {
        this.duration = TimeUnit.SECONDS.toMillis(duration);
        return this;
    }

    public ProjectileEffect killExpiry(boolean killAfterExpiry) {
        this.killAfterExpiry = killAfterExpiry;
        return this;
    }

    public ProjectileEffect expiry(Consumer<Projectile> expiry) {
        this.expiryFunction = expiry;
        return this;
    }

    public void start() {
        Preconditions.checkNotNull(trailingEffect);

        new BukkitRunnable() {

            private long expiryTime = System.currentTimeMillis() + duration;

            @Override
            public void run() {
                if (projectile.isDead() || expiryTime <= System.currentTimeMillis()) {
                    cancel();
                    return;
                }

                if (color != null) {
                    trailingEffect.display(color, projectile.getLocation(), VISUAL_EFFECT_DISTANCE);
                } else {
                    trailingEffect.display(0, 0, 0, 0, 1, projectile.getLocation(), VISUAL_EFFECT_DISTANCE);
                }
            }
            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (expiryFunction != null) {
                    expiryFunction.accept(projectile);
                }
                super.cancel();
            }
        }.runTaskTimer(Brawl.getInstance(), 1L, intervalTicks);
    }
}
