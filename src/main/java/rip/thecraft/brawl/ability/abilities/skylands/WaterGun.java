package rip.thecraft.brawl.ability.abilities.skylands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.ParticleEffect;

import java.util.ArrayList;
import java.util.List;



// TODO Ability WIP
// TODO Make block changes global reference
@AbilityData(
        name = "water Gun",
        color = ChatColor.AQUA,
        icon = Material.INK_SACK,
        data = 12
)
public class WaterGun extends Ability implements Listener {

    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, 120, 4);

    @AbilityProperty(id = "land-particle")
    public ParticleEffect landParticle = ParticleEffect.WATER_SPLASH;

    @AbilityProperty(id = "land-sound")
    public Sound landSound = Sound.SPLASH2;

    @AbilityProperty(id = "activate-particle")
    public ParticleEffect activateParticle = ParticleEffect.WATER_SPLASH;

    @AbilityProperty(id = "activate-sound")
    public Sound activateSound = Sound.SPLASH;

    @AbilityProperty(id = "radius")
    public double radius = 1.6;

    @AbilityProperty(id = "delay")
    public int delay = 5;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.WATER, (byte) 0);
        block.setMetadata("watergun", new FixedMetadataValue(Brawl.getInstance(), player.getUniqueId()));
        block.setDropItem(false);
        block.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));

        new BukkitRunnable() {

            long timestamp = System.currentTimeMillis();
            Player hit;

            @Override
            public void run() {
                if ((System.currentTimeMillis() - timestamp) > 750L) {
                    cancel();
                    return;
                }

                if (block.isDead()) {
                    cancel();
                    stuck(hit != null ? hit.getLocation() : block.getLocation());
                    return;
                }

                block.getNearbyEntities(1, 2, 1).stream().filter(other -> other instanceof Player && !player.equals(other)).findAny().ifPresent(player -> {
                    hit = (Player) player;
                    stuck(hit != null ? hit.getLocation() : block.getLocation());
                    cancel();
                });

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (block == null || block.isDead() || !block.isValid()) {
                    block.remove();
                }
                super.cancel();
            }
        }.runTaskTimer(Brawl.getInstance(), 5L, 5L);

    }

    private void stuck(Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(0.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 1.0D));

        List<Location> changedBlocks = new ArrayList<>();
        for (Location loc : locations) {
            Block state = loc.getBlock();

            if (state.getType() == Material.AIR) {
                state.setMetadata("watergun", new FixedMetadataValue(Brawl.getInstance(), state.getType().name()));
                state.setType(Material.WATER);
                changedBlocks.add(loc);
            }
        }

        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            for (Location loc : changedBlocks) {
                Block state = loc.getBlock();

                if (state.getType() == Material.WATER) {
                    state.setType(Material.AIR);
                }
            }
        }, 120L);

    }
}
