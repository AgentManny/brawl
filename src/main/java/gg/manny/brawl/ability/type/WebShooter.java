package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WebShooter extends Ability {

    private final Brawl plugin;

    @Override
    public Material getType() {
        return Material.WEB;
    }

    @Override
    public String getName() {
        return "Web Shooter";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.WHITE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);


        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.WEB, (byte)0);
        block.setMetadata("webshooter", new FixedMetadataValue(plugin, player.getUniqueId()));
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
        }.runTaskTimer(plugin, 2L, 2L);

    }


    private void stuck(Location location) {
        if(location.getBlock().isLiquid()) {
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
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Location loc : locations) {
                Block state = loc.getBlock();

                if (state.getType() == Material.WEB) {
                    state.setType(Material.AIR);
                }
            }

        }, 120L);

    }

}
