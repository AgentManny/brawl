package rip.thecraft.brawl.ability.abilities.classic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.AbilityGroundHandler;
import rip.thecraft.brawl.ability.handlers.AbilitySneakHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.List;

@AbilityData(
        name = "Stomper",
        description = "Launch yourself into the air and then sneak to thrust yourself down, dealing massive damage to whoever is on the ground below you.",
        icon = Material.ANVIL,
        color = ChatColor.YELLOW
)
public class Stomper extends Ability implements Listener, AbilityGroundHandler, AbilitySneakHandler {

    private static final String STOMPER_METADATA = "Stomper";
    private static final String CHARGE_METADATA = "StomperCharge";

    @AbilityProperty(id = "impact-distance", description = "Radius of nearby players to damage on impact")
    public int impactDistance = 5;

    @AbilityProperty(id = "boost-direction", description = "Boost the direction the player is facing")
    public boolean boostDirection = false;

    @AbilityProperty(id = "damage-reduction", description = "Damage calculated by fall damage is divided")
    public double damageReduction = 3.5;

    @AbilityProperty(id = "fall-damage-reduction", description = "Reduces inflicted fall distance damage")
    public double fallDamageReduction = 3.2;

    @AbilityProperty(id = "boost", description = "Launch multiplier into the air (Y)")
    public double boost = 3.2;

    @AbilityProperty(id = "multiplier", description = "Launch multiplier in direction player is facing")
    public double multiplier = 1.25;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;

        if (player.getLocation().getBlockY() >= 150 || player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
            player.sendMessage(ChatColor.RED + "You can't use this ability here!");
            return;
        }

        if (!player.hasMetadata(STOMPER_METADATA)) {
            Vector vector = boostDirection ? player.getLocation().getDirection().clone()
                    .multiply(multiplier)
                    .setY(boost) : new Vector(0, boost, 0);

            player.setFireTicks(0); // Prevent fire from interfering with velocity
            player.setVelocity(vector);
            player.setMetadata(STOMPER_METADATA, new FixedMetadataValue(Brawl.getInstance(), null));
            player.setMetadata(CHARGE_METADATA, new FixedMetadataValue(Brawl.getInstance(), System.currentTimeMillis()));

            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 0.0F);
        }
    }

    private void thrust(Player player) {
        if (player.hasMetadata(STOMPER_METADATA) && player.hasMetadata(CHARGE_METADATA)) {
            long timestamp = player.getMetadata(CHARGE_METADATA).get(0).asLong();
            if (System.currentTimeMillis() - timestamp <= 250L) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "STOMPER " + ChatColor.GRAY + "Not enough momentum to thrust downward. (wait longer)");
                return;
            }
            player.removeMetadata(CHARGE_METADATA, Brawl.getInstance());

            player.setVelocity(player.getLocation().getDirection().setY(player.getVelocity().getY() - boost).multiply(multiplier + 0.75));

            player.playSound(player.getLocation(), Sound.BAT_LOOP, 1.0F, 0.0F);
            ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, player.getLocation(), EFFECT_DISTANCE);
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        // Patch - Prevent other velocity when CHARGING UP (from player damage)
        if (!player.isDead() && player.hasMetadata(CHARGE_METADATA)) {
            long timestamp = player.getMetadata(CHARGE_METADATA).get(0).asLong();
            if (System.currentTimeMillis() - timestamp <= 10L) return;
            event.setCancelled(true);
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
            player.removeMetadata(STOMPER_METADATA, Brawl.getInstance());
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 0.0F);
            player.setFallDistance((float) (player.getFallDistance() / fallDamageReduction)); // Fall damage should still apply to stompers but be reduced
            addCooldown(player); // Reset the cooldown
        }
    }

    @Override
    public void onSneak(Player player, boolean sneaking) {
        thrust(player);
    }

    @Override
    public void onDeactivate(Player player) {
        player.removeMetadata(STOMPER_METADATA, Brawl.getInstance());
        player.removeMetadata(CHARGE_METADATA, Brawl.getInstance());
    }
}
