package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.util.item.type.InventoryType;
import gg.manny.pivot.util.ErrorType;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
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


}
