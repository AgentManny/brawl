package rip.thecraft.brawl.ability.abilities.classic;

import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.List;

public class Stomper extends Ability implements Listener {

    private static final String STOMPER_METADATA = "Stomper";
    private static final String CHARGE_METADATA = "StomperCharge";

    private double impactDistance = 5;
    private double damageReduction = 3.25;

    private double boost = 3;
    private double multiplier = 1.25;

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
        if (hasCooldown(player, true)) return;

        if (player.getLocation().getBlockY() >= 150) {
            player.sendMessage(ChatColor.RED + "You can't use this ability here!");
            return;
        }

        if (!player.hasMetadata(STOMPER_METADATA)) {
            Vector directionVector = player.getLocation().getDirection().clone()
                    .multiply(multiplier)
                    .setY(boost);

            player.setMetadata(STOMPER_METADATA, new FixedMetadataValue(Brawl.getInstance(), null));
            player.setMetadata(CHARGE_METADATA, new FixedMetadataValue(Brawl.getInstance(), null));

            player.setVelocity(directionVector);

            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 0.0F);
        }
    }

    @Override
    public void onGround(Player player, boolean onGround) {
        if (onGround && player.hasMetadata(STOMPER_METADATA)) {
            onDeactivate(player); // Removes player metadata

            double baseDamage = Math.min(50, player.getFallDistance()) / damageReduction;

            List<Player> nearbyPlayers = PlayerUtil.getNearbyPlayers(player, impactDistance);
            for (Player nearbyPlayer : nearbyPlayers) {
                nearbyPlayer.damage(baseDamage / (nearbyPlayer.isSneaking() ? 2 : 1));
            }

            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 1, player.getLocation(), EFFECT_DISTANCE);
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 0.0F);
            player.setFallDistance(0);
            addCooldown(player); // Reset the cooldown
        }
    }

    @Override
    public void onSneak(Player player, boolean sneaking) {
        if (player.hasMetadata(STOMPER_METADATA) && player.hasMetadata(CHARGE_METADATA)) {
            player.removeMetadata(CHARGE_METADATA, Brawl.getInstance());

            player.setVelocity(player.getLocation().getDirection().setY(player.getVelocity().getY() - boost).multiply(multiplier + 0.75));

            player.playSound(player.getLocation(), Sound.BAT_LOOP, 1.0F, 0.0F);
            ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, player.getLocation(), EFFECT_DISTANCE);
        }
    }

    @Override
    public void onDeactivate(Player player) {
        player.removeMetadata(STOMPER_METADATA, Brawl.getInstance());
        player.removeMetadata(CHARGE_METADATA, Brawl.getInstance());
    }

    @Override
    public JsonObject toJson() {
        JsonObject data = super.toJson();
        data.addProperty("impact-distance", impactDistance);
        data.addProperty("boost", boost);
        data.addProperty("multiplier", multiplier);
        data.addProperty("damage-reduction", damageReduction);

        return data;
    }

    @Override
    public void fromJson(JsonObject object) {
        impactDistance = object.get("impact-distance").getAsDouble();
        damageReduction = object.get("damage-reduction").getAsDouble();
        multiplier = object.get("multiplier").getAsDouble();
        boost = object.get("boost").getAsDouble();
    }
}
