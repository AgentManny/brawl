package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.BlockProjectileHitBlockHandler;
import rip.thecraft.brawl.ability.handlers.BlockProjectileHitHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.moreprojectiles.event.BlockProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.projectile.BlockProjectile;

import java.util.ArrayList;
import java.util.List;

@AbilityData(
        name = "Web Shooter",
        description = "Shoot a cluster of sticky webs to trap your enemies.",
        icon = Material.WEB,
        color = ChatColor.WHITE
)
public class WebShooter extends Ability implements BlockProjectileHitHandler, BlockProjectileHitBlockHandler {

    @Override
    public void cleanup() {
        storedLocations.forEach(state -> {
            if (state.getType() == Material.WEB) {
                state.getBlock().setType(Material.AIR);
            }
        });
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new BlockProjectile("webshooter", player, Material.WEB.getId(), 0, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.SPIDER_WALK, 2f, 2f);
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
        if(RegionType.SAFEZONE.appliesTo(location)) return;

        if (location.getBlock().isLiquid()) {
            location = location.add(0, 1.0D, 0);
        }

        List<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(-1.0D, 0.0D, 0.0D));
        locations.add(location.clone().add(0.0D, 0.0D, 1.0D));
        locations.add(location.clone().add(0.0D, 0.0D, -1.0D));
        locations.add(location.clone().add(1.0D, 0.0D, 0.0D));
        locations.add(location.clone());

        for (Location loc : locations) {
            Block state = loc.getBlock();

            if (state.getType() == Material.AIR) {
                state.setType(Material.WEB);
            }
            storedLocations.add(state.getState());
        }

        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            for (Location loc : locations) {
                Block state = loc.getBlock();

                if (state.getType() == Material.WEB) {
                    state.setType(Material.AIR);
                }
                storedLocations.remove(state.getState());
            }

        }, 120L);
    }

}