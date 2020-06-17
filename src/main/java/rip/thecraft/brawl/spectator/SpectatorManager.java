package rip.thecraft.brawl.spectator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.PlayerUtils;

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
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game == null) return;
            if (game.getSpectators().contains(player.getUniqueId()) && spectators.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }

            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (game.getSpectators().contains(damager.getUniqueId()) && spectators.contains(damager.getUniqueId()) ) {
                    event.setCancelled(true);
                }
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
        if (getSpectating(event.getPlayer()) == SpectatorType.GAME) {
            Brawl.getInstance().getSpectatorManager().removeSpectator(event.getPlayer().getUniqueId(), Brawl.getInstance().getGameHandler().getActiveGame(), false);
            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null) {
                game.getSpectators().remove(event.getPlayer().getUniqueId());
            }
        }
    }


    public void bug(Game game) {
        for (UUID spectator : this.spectators) {
            removeSpectator(spectator, game, Bukkit.getPlayer(spectator) == null);
        }
    }

    public void removeSpectator(UUID uuid, Game game, boolean disconnected) {
        Player player = Bukkit.getPlayer(uuid);
        if (inSpectator(uuid)) {
            if (!disconnected && player != null) {

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

                NametagHandler.reloadPlayer(player);
                NametagHandler.reloadOthersFor(player);

            }

        }
        spectators.remove(uuid);
        if (game != null) {
            game.getSpectators().remove(uuid);

        }
    }

    public void addSpectator(Player player, Game game) {
        if (player != null) {

            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online == player) continue;
                online.hidePlayer(player); // Spectator mode
            }

            Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPECTATOR);

            NametagHandler.reloadPlayer(player);
            NametagHandler.reloadOthersFor(player);

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
