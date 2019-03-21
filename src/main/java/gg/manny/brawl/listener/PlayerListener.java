package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.region.RegionType;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.util.ErrorType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ErrorType.SERVER_STILL_LOADING.getMessage());
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem() && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            ItemStack item = event.getItem();
            if (item != null && item.getType() != null && item.getType() == Material.ENDER_PEARL) {
                if (playerData.hasCooldown("ENDERPEARL")) {
                    player.sendMessage(Locale.PLAYER_ABILITY_COOLDOWN.format(playerData.getCooldown("ENDERPEARL").getTimeLeft()));
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }
                playerData.addCooldown("ENDERPEARL", TimeUnit.SECONDS.toMillis(16));
                return;
            }
            Kit kit = playerData.getSelectedKit();

            if (kit != null) {
                for (Ability ability : kit.getAbilities()) {
                    if (BrawlUtil.match(ability.getIcon(), event.getItem())) {
                        if (RegionType.SAFEZONE.containsLocation(player.getLocation())) {
                            player.sendMessage(Locale.PLAYER_ABILITY_ERROR_SAFEZONE.format());
                            return;
                        }
                        ability.onActivate(player);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(20.0D);
        player.setHealth(2.0D);
        player.teleport(plugin.getLocationByName("SPAWN"));

        plugin.getItemHandler().apply(player, InventoryType.SPAWN);

        PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName());
        playerData.setSpawnProtection(true);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.fromJSON(plugin.getPlayerDataHandler().getDocument(player.getUniqueId()));
            plugin.getPlayerDataHandler().create(playerData, false);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.save();
            plugin.getPlayerDataHandler().remove(playerData);
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        if (!playerData.isBuild()) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        if (!playerData.isBuild()) {
            event.setCancelled(true);
        }
    }


}
