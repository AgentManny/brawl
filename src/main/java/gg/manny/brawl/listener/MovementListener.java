package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.region.RegionType;
import gg.manny.spigot.handler.SimpleMovementHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class MovementListener implements SimpleMovementHandler, Listener {

    private final Brawl plugin;

    @Override
    public void onPlayerMove(Player player, Location to, Location from) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        if (playerData.isSpawnProtection()) {

            if(RegionType.SAFEZONE.containsLocation(from) && !RegionType.SAFEZONE.containsLocation(to)) {
                playerData.setSpawnProtection(false);
                if (playerData.getSelectedKit() == null) {
                    Kit selectedKit = Brawl.getInstance().getKitHandler().getDefaultKit();
                    Kit previousKit = playerData.getPreviousKit();

                    if (previousKit != null && playerData.hasKit(previousKit)) {
                        selectedKit = previousKit;
                    }

                    selectedKit.apply(player, true, true);
                }

                player.sendMessage(Locale.PLAYER_PROTECTION_REMOVED.format());
            }

        } else {

            if(!RegionType.SAFEZONE.containsLocation(from) && RegionType.SAFEZONE.containsLocation(to)) {
                player.teleport(from);
                player.setVelocity(from.toVector().subtract(to.toVector()).normalize()
                        .multiply(1.25)
                        .add(new Vector(0, 0.5, 0))
                        .setY(.1));
            }

        }

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {

            Player player = event.getPlayer();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            if (!playerData.isSpawnProtection()) {
                Location to = event.getTo();
                Location from = event.getFrom();
                if (!RegionType.SAFEZONE.containsLocation(from) && RegionType.SAFEZONE.containsLocation(to)) {
                    if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                        player.sendMessage(Locale.TELEPORT_ERROR_ENDERPEARL.format());
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        player.updateInventory();
                    }

                    event.setTo(event.getFrom());
                    event.setCancelled(true);
                    player.setVelocity(from.toVector().subtract(to.toVector()).normalize().multiply(1.25).add(new Vector(0, 0.5, 0)).setY(.1));
                }
            }
        }
    }

}
