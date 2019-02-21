package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.util.inventory.BlockUtil;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.particle.ParticleEffect;

public class Stomper extends Ability implements Listener {

    private final Brawl brawl;

    private ParticleEffect activateParticle = ParticleEffect.FOOTSTEP;
    private Sound activateSound = Sound.BAT_TAKEOFF;

    private ParticleEffect movementParticle = ParticleEffect.SMOKE_NORMAL;

    private ParticleEffect landParticle = ParticleEffect.EXPLOSION_HUGE;
    private Sound landSound = Sound.ANVIL_LAND;

    private ParticleEffect sneakParticle = ParticleEffect.CLOUD;
    private Sound sneakSound = Sound.BAT_LOOP;

    private double boost = 5;
    private double multiplier = 1.25;

    public Stomper(Brawl brawl) {
        super("Stomper", new ItemBuilder(Material.ANVIL)
                .name(CC.GRAY + "\u00bb " + CC.YELLOW + CC.BOLD + "Stomper" + CC.GRAY + " \u00ab")
                .create()
        );

        this.brawl = brawl;
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
            this.activateParticle.send(brawl.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
        }

        this.brawl.getServer().getScheduler().runTaskLater(this.brawl, () -> new StomperTask(player).runTaskTimer(this.brawl, 2L, 2L), 10L);
    }


    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("activateParticle", this.activateParticle == null ? null : this.activateParticle.name());
        object.addProperty("activateSound", this.activateSound == null ? null : this.activateSound.name());

        object.addProperty("movementParticle", this.movementParticle == null ? null : this.movementParticle.name());
        object.addProperty("landParticle", this.landParticle == null ? null : this.landParticle.name());
        object.addProperty("landSound", this.landSound == null ? null : this.landSound.name());
        object.addProperty("sneakParticle", this.sneakParticle == null ? null : this.sneakParticle.name());
        object.addProperty("sneakSound", this.sneakSound == null ? null : this.sneakSound.name());
        object.addProperty("boost", this.boost);
        object.addProperty("multiplier", this.multiplier);
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        this.activateParticle = object.get("activateParticle") == null ? null : ParticleEffect.valueOf(object.get("activateParticle").getAsString());
        this.activateSound = object.get("activateSound") == null ? null : Sound.valueOf(object.get("activateSound").getAsString());

        this.movementParticle = object.get("movementParticle") == null ? null : ParticleEffect.valueOf(object.get("movementParticle").getAsString());

        this.landParticle = object.get("landParticle") == null ? null : ParticleEffect.valueOf(object.get("landParticle").getAsString());
        this.landSound = object.get("landSound") == null ? null : Sound.valueOf(object.get("landSound").getAsString());

        this.sneakParticle = object.get("sneakParticle") == null ? null : ParticleEffect.valueOf(object.get("sneakParticle").getAsString());
        this.sneakSound = object.get("sneakSound") == null ? null : Sound.valueOf(object.get("sneakSound").getAsString());

        this.boost = object.get("boost").getAsDouble();
        this.multiplier = object.get("multiplier").getAsDouble();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            if (this.hasEquipped(player)) {
                double damage = event.getDamage();
                for (Player nearby : BrawlUtil.getNearbyPlayers(player, Math.min(5, player.getFallDistance()))) {
                    nearby.damage(nearby.isSneaking() ? ((damage / (this.multiplier + this.boost) < 10) ? 10 : (damage / (this.multiplier + this.boost))) : (damage / this.multiplier), event.getEntity());
                }
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
    }

    @RequiredArgsConstructor
    private class StomperTask extends BukkitRunnable {

        private final Player player;
        private boolean sneaked = false;

        @Override
        public void run() {
            if (BlockUtil.isOnGround(player.getLocation(), 1)) {
                cancel();
                if (landParticle != null) {
                    landParticle.send(brawl.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
                }

                if (landSound != null) {
                    player.playSound(player.getLocation(), landSound, 1.0F, 0.0F);
                }
                return;
            }

            if (!sneaked && player.isSneaking()) {
                sneaked = true; //prevent spam sneak.
                player.setVelocity(player.getLocation().getDirection().setY(player.getVelocity().getY() - boost).multiply(multiplier));
                if (sneakSound != null) {
                    player.playSound(player.getLocation(), sneakSound, 1.0F, 0.0F);
                }
                if (sneakParticle != null) {
                    sneakParticle.send(brawl.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
                }
                return;
            }

            if (movementParticle != null) {
                movementParticle.send(brawl.getServer().getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0, 1);
            }
        }

    }

}
