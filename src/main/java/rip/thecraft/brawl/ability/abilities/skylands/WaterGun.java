package rip.thecraft.brawl.ability.abilities.skylands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.BlockProjectileHitBlockHandler;
import rip.thecraft.brawl.ability.handlers.BlockProjectileHitHandler;
import rip.thecraft.brawl.ability.handlers.ItemProjectileHitBlockHandler;
import rip.thecraft.brawl.ability.handlers.ItemProjectileHitHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.moreprojectiles.event.BlockProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.event.ItemProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.projectile.BlockProjectile;
import rip.thecraft.brawl.util.moreprojectiles.projectile.ItemProjectile;

import java.util.ArrayList;
import java.util.List;



// TODO Ability WIP
// TODO Make block changes global reference
@AbilityData(
        name = "Water Gun",
        description = "Shoot a heap of water that will slow your enemies.",
        color = ChatColor.AQUA,
        icon = Material.INK_SACK,
        data = 12
)
public class WaterGun extends Ability implements Listener, BlockProjectileHitBlockHandler, BlockProjectileHitHandler {

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
    public void cleanup() {
        storedLocations.forEach(state -> {
            if (state.getType() == Material.STATIONARY_WATER) {
                state.getBlock().setType(Material.AIR);
            }
        });
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new BlockProjectile("watergun", player, Material.ICE.getId(), 0, 1f); //throws ice atm, couldn't get it to throw a water block
    }

    @Override
    public boolean onBlockProjectileHitBlock(Player shooter, BlockProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();
        stuck(hitBlock.getLocation());
        return false;
    }

    @Override
    public boolean onBlockProjectileHit(Player shooter, Player hit, BlockProjectileHitEvent event) {
        Block block = hit.getLocation().getBlock();
        stuck(block.getLocation());
        return false;
    }

    private List<BlockState> storedLocations = new ArrayList<>();

    private void stuck(Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(1.0D, 0, -1.0D));
        locations.add(location.clone().add(-1.0D, 0, -1.0D));
        locations.add(location.clone().add(1.0D, 0, 1.0D));
        locations.add(location.clone().add(-1.0D, 0, 1.0D));
        locations.add(location.clone().add(0.0D, 0, 0.0D));
        locations.add(location.clone().add(-1.0D, 0, 0.0D));
        locations.add(location.clone().add(1.0D, 0, 0.0D));
        locations.add(location.clone().add(0.0D, 0, -1.0D));
        locations.add(location.clone().add(0.0D, 0, 1.0D));

        List<Location> changedBlocks = new ArrayList<>();
        for (Location loc : locations) {
            Block state = loc.getBlock();

            if (state.getType() == Material.AIR) {
                state.setMetadata("watergun", new FixedMetadataValue(Brawl.getInstance(), state.getType().name()));
                state.setType(Material.STATIONARY_WATER);
                changedBlocks.add(loc);
            }

            storedLocations.add(state.getState());
        }

        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            for (Location loc : changedBlocks) {
                Block state = loc.getBlock();

                if (state.getType() == Material.STATIONARY_WATER || state.isLiquid()) {
                    state.setType(Material.AIR);
                }

                storedLocations.remove(state.getState());
            }
        }, 120L);

    }
}
