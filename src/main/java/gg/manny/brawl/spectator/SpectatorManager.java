package gg.manny.brawl.spectator;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.staff.StaffMode;
import gg.manny.pivot.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class SpectatorManager implements Listener {

    private final Brawl plugin;

    private final Set<UUID> spectators = new HashSet<>();
    private final Map<UUID, UUID> following = new HashMap<>();

    public SpectatorManager(Brawl plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                event.getPlayer().hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                event.getPlayer().showPlayer(player);
            }
        }
    }

    public void removeSpectator(UUID uuid, Game game, boolean disconnected) {
        Player player = Bukkit.getPlayer(uuid);
        if (inSpectator(uuid)) {
            if (!disconnected) {
                player.setAllowFlight(false);
                player.setFlying(false);

                player.teleport(plugin.getLocationByName("SPAWN"));
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                playerData.setSpawnProtection(true);
                playerData.setEvent(false);
                playerData.setDuelArena(false);

                if (!StaffMode.hasStaffMode(player)) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.showPlayer(player); // Spectator mode
                    }
                }

                PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
                Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);

            }
            spectators.remove(player.getUniqueId());
            game.getSpectators().remove(player.getUniqueId());



        }
    }

    public void addSpectator(Player player, Game game) {
        if (player != null) {

            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.hidePlayer(player); // Spectator mode
            }

            Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPECTATOR);
            game.getSpectators().add(player.getUniqueId());
            spectators.add(player.getUniqueId());
        }
    }

    public boolean inSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public boolean inSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public SpectatorType getSpectating(Player player) {
        SpectatorType spectator = SpectatorType.NONE;
        if (spectators.contains(player.getUniqueId())) {
            if (following.containsKey(player.getUniqueId())) {
                spectator = SpectatorType.PLAYER;
            } else if (plugin.getGameHandler().getActiveGame() != null && plugin.getGameHandler().getActiveGame().getSpectators().contains(player.getUniqueId())) {
                spectator = SpectatorType.GAME;
            } else if (plugin.getMatchHandler().getSpectatingMatch(player) != null) {
                spectator = SpectatorType.MATCH;
            }
        }
        return spectator;
    }

}
