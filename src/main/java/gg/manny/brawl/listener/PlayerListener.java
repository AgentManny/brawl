package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.duelarena.match.Match;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.region.RegionType;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.util.ErrorType;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
                    player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + playerData.getCooldown("ENDERPEARL").getTimeLeft() + ChatColor.RED + " before using this again.");
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }
                playerData.addCooldown("ENDERPEARL", TimeUnit.SECONDS.toMillis(16));
            }
            Match match = plugin.getMatchHandler().getMatch(player);
            Kit kit = match != null && match.getKit() != null ? match.getKit() : playerData.getSelectedKit();

            if (kit != null) {
                for (Ability ability : kit.getAbilities()) {
                    if (BrawlUtil.match(ability.getIcon(), event.getItem())) {
                        if (RegionType.SAFEZONE.appliesTo(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "You cannot use abilities in spawn.");
                            return;
                        }
                        ability.onActivate(player);
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setCancelled(true);
                        break;
                    }
                }

                for (Killstreak ks : plugin.getKillstreakHandler().getKillstreaks().values()) {
                    if (ks != null && ks.isInteractable() && BrawlUtil.match(ks.getIcon(), event.getItem())) {
                        if (RegionType.SAFEZONE.appliesTo(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "You cannot use killstreak abilities in spawn.");
                            return;
                        }
                        ks.onActivate(player, playerData);
                        if (event.getPlayer().getItemInHand().getAmount() > 1) {
                            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                        } else {
                            event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());

                        }
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setCancelled(true);

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
        player.setHealth(20.0D);
        player.teleport(plugin.getLocationByName("SPAWN"));
        plugin.getItemHandler().apply(player, InventoryType.SPAWN);

        SimpleOfflinePlayer.init(player);
        PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName());
        playerData.setSpawnProtection(true);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.fromDocument(plugin.getPlayerDataHandler().getDocument(player.getUniqueId()));
            plugin.getPlayerDataHandler().create(playerData, false);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        if (playerData.isEvent()) {
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game != null && game.containsPlayer(player)) {
                game.handleElimination(player, event.getPlayer().getLocation(), true);
            }
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.save();
            plugin.getPlayerDataHandler().remove(playerData);
        });
    }


}
