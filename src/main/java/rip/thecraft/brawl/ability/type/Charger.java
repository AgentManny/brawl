package rip.thecraft.brawl.ability.type;

import com.google.gson.JsonObject;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.brawl.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Charger extends Ability implements Listener {

    private final Brawl brawl;

    private HashMap<UUID, Long> chargeup = new HashMap<>();

    private ParticleEffect chargeParticle = ParticleEffect.CLOUD;
    private Sound chargeSound = Sound.CLICK;

    private ParticleEffect activateParticle = ParticleEffect.HUGE_EXPLOSION;
    private Sound activateSound = Sound.HORSE_LAND;

    private double multiplier = 0.5D;
    private int capacity = 5;

    public Charger(Brawl brawl) {
        this.brawl = brawl;
    }

    @Override
    public Map<String, String> getProperties(Player player) {
        Map<String, String> properties = new HashMap<>();
        if (chargeup.containsKey(player.getUniqueId())) {
            long time = System.currentTimeMillis() - this.chargeup.get(player.getUniqueId());
            if (time > (capacity * 1000)) {
                time = (capacity * 1000);
            }
            properties.put("Charge Up", DurationFormatter.getTrailing((time / capacity) * 100) + '%');
        }
        return properties;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
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
                    this.chargeParticle.send(player.getLocation(), 0, 0, 0, 0, 1);
                }
            } else if(this.chargeup.containsKey(player.getUniqueId())) {
                long time = System.currentTimeMillis() - this.chargeup.get(player.getUniqueId());
                if (time > (capacity * 1000)) {
                    time = (capacity * 1000);
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
                    this.activateParticle.send(player.getLocation(), 1, 1);
                }

                this.chargeup.remove(player.getUniqueId());
                this.addCooldown(player);
            }
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("activateParticle", this.activateParticle == null ? null : this.activateParticle.name());
        object.addProperty("activateSound", this.activateSound == null ? null : this.activateSound.name());
        object.addProperty("sneakParticle", this.chargeParticle == null ? null : this.chargeParticle.name());
        object.addProperty("sneakSound", this.chargeSound == null ? null : this.chargeSound.name());
        object.addProperty("capacity", this.capacity);
        object.addProperty("multiplier", this.multiplier);
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        this.activateParticle = object.get("activateParticle") == null ? null : ParticleEffect.valueOf(object.get("activateParticle").getAsString());
        this.activateSound = object.get("activateSound") == null ? null : Sound.valueOf(object.get("activateSound").getAsString());

        this.chargeParticle = object.get("chargeParticle") == null ? null : ParticleEffect.valueOf(object.get("chargeParticle").getAsString());
        this.chargeSound = object.get("chargeSound") == null ? null : Sound.valueOf(object.get("chargeSound").getAsString());

        this.capacity = object.get("capacity").getAsInt();
        this.multiplier = object.get("multiplier").getAsDouble();
    }
}
