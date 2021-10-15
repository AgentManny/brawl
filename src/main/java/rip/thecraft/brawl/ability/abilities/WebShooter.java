package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;

import java.util.ArrayList;
import java.util.List;

@AbilityData(
        name = "Web Shooter",
        description = "Shoot a cluster of sticky webs to trap your enemies.",
        icon = Material.WEB,
        color = ChatColor.WHITE
)
public class WebShooter extends Ability {

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

        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.WEB, (byte) 0);
        block.setMetadata("webshooter", new FixedMetadataValue(Brawl.getInstance(), player.getUniqueId()));
        block.setDropItem(false);
        block.setVelocity(player.getEyeLocation().getDirection().multiply(1.25));
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

                block.getNearbyEntities(1, 1.5, 1).stream().filter(other -> other instanceof Player && !player.equals(other)).findAny().ifPresent(player -> {
                    hit = (Player) player;
                    stuck(hit != null ? hit.getLocation() : block.getLocation());

                    cancel();
                });

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (block != null && !block.isDead() && block.isValid()) {
                    block.remove();
                }
                super.cancel();
            }
        }.runTaskTimer(Brawl.getInstance(), 5L, 5L);

    }

    private List<BlockState> storedLocations = new ArrayList<>();

    private void stuck(Location location) {
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