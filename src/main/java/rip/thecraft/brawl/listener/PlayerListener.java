package rip.thecraft.brawl.listener;

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
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.killstreak.Killstreak;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.falcon.util.ErrorType;
import rip.thecraft.spartan.util.PlayerUtils;

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

                        if (!ability.bypassAbilityPreventZone() && RegionType.NO_ABILITY_ZONE.appliesTo(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "You cannot use abilities in area.");
                            return;
                        }

                        ability.onActivate(player);
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setCancelled(true);
                        player.updateInventory();
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

     //   player.setPlayerListName(player.getDisplayName());
        plugin.getItemHandler().apply(player, InventoryType.SPAWN);

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
        playerData.setSelectedKit(null);

        if (playerData.isEvent()) {
            GameLobby lobby = plugin.getGameHandler().getLobby();
            if (lobby != null && lobby.getPlayers().contains(player.getUniqueId())) {
                lobby.leave(player.getUniqueId());
            }

            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.containsPlayer(player)) {
                game.handleElimination(player, event.getPlayer().getLocation(), GameElimination.QUIT);
            }
        } else if (playerData.hasCombatLogged()) {
            Player damageSource = PlayerUtils.getDamageSource(player);
            if (damageSource != null) {
                playerData.getSpawnData().killed(damageSource);
            }
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.save();
            plugin.getPlayerDataHandler().remove(playerData);
        });
    }


}
