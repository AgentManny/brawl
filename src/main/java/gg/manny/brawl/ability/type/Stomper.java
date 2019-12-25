package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.BlockUtil;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.brawl.util.ParticleEffect;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Stomper extends Ability implements Listener {

    private final Brawl brawl;

    private ParticleEffect activateParticle = ParticleEffect.FOOTSTEP;
    private Sound activateSound = Sound.BAT_TAKEOFF;

    private Effect movementEffect = Effect.SMOKE;

    private ParticleEffect landParticle = ParticleEffect.HUGE_EXPLOSION;
    private Sound landSound = Sound.ANVIL_LAND;

    private ParticleEffect sneakParticle = ParticleEffect.CLOUD;
    private Sound sneakSound = Sound.BAT_LOOP;

    private double boost = 5;
    private double multiplier = 1.25;

    public Stomper(Brawl brawl) {
        this.brawl = brawl;
    }

    @Override
    public Material getType() {
        return Material.ANVIL;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        player.setVelocity(player.getLocation().getDirection().normalize().setY(player.getVelocity().getY() + this.boost).multiply(this.multiplier));

        if (this.activateSound != null) {
            player.playSound(player.getLocation(), this.activateSound, 1.0F, 0.0F);
        }

        if (this.activateParticle != null) {
            this.activateParticle.send(player.getLocation(), 0, 0, 0, 0, 1);
        }

        this.brawl.getServer().getScheduler().runTaskLater(this.brawl, () -> new StomperTask(player).runTaskTimer(this.brawl, 2L, 2L), 10L);
    }


    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("activate-particle", this.activateParticle == null ? null : this.activateParticle.name());
        object.addProperty("activate-sound", this.activateSound == null ? null : this.activateSound.name());

        object.addProperty("movement-particle", this.movementEffect == null ? null : this.movementEffect.name());
        object.addProperty("land-particle", this.landParticle == null ? null : this.landParticle.name());
        object.addProperty("land-sound", this.landSound == null ? null : this.landSound.name());
        object.addProperty("sneak-particle", this.sneakParticle == null ? null : this.sneakParticle.name());
        object.addProperty("sneak-sound", this.sneakSound == null ? null : this.sneakSound.name());
        object.addProperty("boost", this.boost);
        object.addProperty("multiplier", this.multiplier);
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        this.activateParticle = object.get("activate-particle") == null ? null : ParticleEffect.valueOf(object.get("activate-particle").getAsString());
        this.activateSound = object.get("activate-sound") == null ? null : Sound.valueOf(object.get("activate-sound").getAsString());

        this.movementEffect = object.get("movement-effect") == null ? null : Effect.valueOf(object.get("movement-effect").getAsString());

        this.landParticle = object.get("land-particle") == null ? null : ParticleEffect.valueOf(object.get("land-particle").getAsString());
        this.landSound = object.get("land-sound") == null ? null : Sound.valueOf(object.get("land-sound").getAsString());

        this.sneakParticle = object.get("sneak-particle") == null ? null : ParticleEffect.valueOf(object.get("sneak-particle").getAsString());
        this.sneakSound = object.get("sneak-sound") == null ? null : Sound.valueOf(object.get("sneak-sound").getAsString());

        this.boost = object.get("boost").getAsDouble();
        this.multiplier = object.get("multiplier").getAsDouble();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            if (this.hasEquipped(player) && !brawl.getPlayerDataHandler().getPlayerData(player).isNoFallDamage()) {
                double damage = event.getDamage();
                for (Player nearby : BrawlUtil.getNearbyPlayers(player, Math.min(5, player.getFallDistance()))) {
                    nearby.damage(nearby.isSneaking() ? ((damage / (this.multiplier + this.boost) < 10) ? 10 : (damage / (this.multiplier + this.boost))) : (damage / this.multiplier), event.getEntity());
                }
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {

        }
    }

    @Data
    @RequiredArgsConstructor
    private class StomperTask extends BukkitRunnable {

        private long startedAt = System.currentTimeMillis();

        private final Player player;
        private boolean sneaked = false;

        @Override
        public void run() {
            if (player == null || Bukkit.getPlayer(player.getName()) == null || brawl.getPlayerDataHandler().getPlayerData(player) == null || brawl.getPlayerDataHandler().getPlayerData(player).isNoFallDamage()) {
                cancel();
                return;
            }
            if ((System.currentTimeMillis() - startedAt > 200) && BlockUtil.isOnGround(player.getLocation(), 1)) {
                cancel();
                if (landParticle != null) {
                    landParticle.send(player.getLocation(), 0, 0, 0, 0, 1);
                }

                if (landSound != null) {
                    player.playSound(player.getLocation(), landSound, 1.0F, 0.0F);
                }
                return;
            }

            if (!sneaked && player.isSneaking()) {
                sneaked = true; //prevent spam sneak.
                player.setVelocity(player.getLocation().getDirection().setY(player.getVelocity().getY() - boost).multiply(multiplier + 0.75));
                if (sneakSound != null) {
                    player.playSound(player.getLocation(), sneakSound, 1.0F, 0.0F);
                }
                if (sneakParticle != null) {
                    sneakParticle.send(player.getLocation(), 0, 0, 0, 0, 1);
                }
                return;
            }

            if (movementEffect != null) {
                ParticleEffect.send(movementEffect, player.getLocation(), 1);
            }
        }

    }

}
