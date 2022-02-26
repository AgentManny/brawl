package rip.thecraft.brawl.spawn.launchpad;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.data.SpawnData;
import rip.thecraft.brawl.util.Tasks;

import static rip.thecraft.brawl.spawn.launchpad.LaunchpadHandler.JUMP_METADATA;

@RequiredArgsConstructor
public class LaunchpadListener implements Listener {

    private final LaunchpadHandler launchpadHandler;
    private final Brawl plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() == Material.EMERALD_BLOCK && player.hasMetadata(JUMP_METADATA)) {
            event.setCancelled(true);
            Location location = block.getLocation();
            if (!launchpadHandler.getLaunchpad(location).isPresent()) {
                player.sendMessage(ChatColor.GREEN + "Added launchpad: " + ChatColor.WHITE + location.toVector().toBlockVector().toString());
                Tasks.schedule(() -> player.sendBlockChange(location, Material.EMERALD_BLOCK, (byte) 0), 4);
                launchpadHandler.getLocations().add(location);
                launchpadHandler.save();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player.hasMetadata(JUMP_METADATA)) {
            launchpadHandler.getLaunchpad(block.getLocation()).ifPresent(location -> {
                player.sendMessage(ChatColor.RED + "Removed launchpad: " + ChatColor.WHITE + location.toVector().toBlockVector().toString());
                player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
                launchpadHandler.getLocations().remove(location);
            });
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            player.getVehicle().remove();
        }
        launchpadHandler.getPendingJumps().remove(player.getUniqueId());
    }

    @EventHandler
    public void onVehicleExit(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Entity vehicle = event.getDismounted();
            if (vehicle.hasMetadata(JUMP_METADATA)) {
                PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
                SpawnData spawnData = playerData.getSpawnData();
                if (spawnData.isJumping()) {
                    event.setCancelled(true);
                } else {
                    spawnData.cancelJump();
                    vehicle.remove();
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Horse || entity instanceof ArmorStand) {
            if (entity.hasMetadata(JUMP_METADATA)) {
                if (entity.getPassenger() == null) {
                    entity.remove();
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

}
