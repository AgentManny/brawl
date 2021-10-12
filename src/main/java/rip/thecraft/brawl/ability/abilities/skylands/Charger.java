package rip.thecraft.brawl.ability.abilities.skylands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.AbilityScoreboardHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.brawl.util.ParticleEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AbilityData(color = ChatColor.GOLD)
public class Charger extends Ability implements Listener, AbilityScoreboardHandler {

    private HashMap<UUID, Long> chargeup = new HashMap<>();

    @AbilityProperty(id = "charge-particle")
    public ParticleEffect chargeParticle = ParticleEffect.CLOUD;

    @AbilityProperty(id = "charge-sound")
    public Sound chargeSound = Sound.CLICK;

    @AbilityProperty(id = "activate-particle")
    public ParticleEffect activateParticle = ParticleEffect.EXPLOSION_LARGE;

    @AbilityProperty(id = "activate-sound")
    public Sound activateSound = Sound.HORSE_LAND;

    @AbilityProperty(id = "impact-multiplier", description = "Impact to throw players away from you")
    public double multiplier = 0.5D;

    @AbilityProperty(id = "capacity", description = "Duration for maximum charge time")
    public int capacity = 5;

    @Override
    public void cleanup() {
        chargeup.clear();
    }

    @Override
    public Map<String, String> getScoreboard(Player player) {
        Map<String, String> properties = new HashMap<>();
        if (chargeup.containsKey(player.getUniqueId())) {
            long time = System.currentTimeMillis() - this.chargeup.get(player.getUniqueId());
            if (time > (capacity * 1000L)) {
                time = (capacity * 1000L);
            }
            properties.put("Charge Up", DurationFormatter.getTrailing((time / capacity) * 100) + '%');
        }
        return properties;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (this.hasEquipped(player)) {
            if (this.hasCooldown(player, event.isSneaking())) return;

            if(event.isSneaking()) {
                 this.chargeup.put(player.getUniqueId(), System.currentTimeMillis());
                if (this.chargeSound != null) {
                    player.playSound(player.getLocation(), this.chargeSound, 1.0F, 0.0F);
                }

                if (this.chargeParticle != null) {
           //         this.chargeParticle.send(player.getLocation(), 0, 0, 0, 0, 1);
                }
            } else if(this.chargeup.containsKey(player.getUniqueId())) {
                long time = System.currentTimeMillis() - this.chargeup.get(player.getUniqueId());
                if (time > (capacity * 1000L)) {
                    time = (capacity * 1000L);
                }
                double kb = multiplier * (time / 1000.);
                for (Player nearby : BrawlUtil.getNearbyPlayers(player, kb)) {
                    if (nearby.isSneaking()) {
                        final Vector velocity = nearby.getLocation().subtract(player.getLocation()).toVector();
                        velocity.setY(velocity.getY() / 3);
                        nearby.setVelocity(velocity.multiply(kb / (1 + velocity.lengthSquared())));
                    }
                }

                if (this.activateSound != null) {
                    player.playSound(player.getLocation(), this.activateSound, 1.0F, 0.0F);
                }

                if (this.activateParticle != null) {
                //    this.activateParticle.send(player.getLocation(), 1, 1);
                }

                this.chargeup.remove(player.getUniqueId());
                this.addCooldown(player);
            }
        }
    }
}
