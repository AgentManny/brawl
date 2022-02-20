package rip.thecraft.brawl.spawn.jump;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.data.SpawnData;

public class JumpHandler implements Listener {

    public static final String JUMP_METADATA = "JUMP";

    private Brawl plugin;

    public JumpHandler(Brawl plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            player.getVehicle().remove();
        }
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
