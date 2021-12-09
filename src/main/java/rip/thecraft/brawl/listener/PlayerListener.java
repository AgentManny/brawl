package rip.thecraft.brawl.listener;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
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
import rip.thecraft.brawl.util.VisibilityUtils;
import rip.thecraft.falcon.util.ErrorType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.PlayerUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

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

            boolean spawnProtection = RegionType.SAFEZONE.appliesTo(player.getLocation());
            boolean cancelInteraction = false;
            if (item != null && spawnProtection) {
                if (item.getType() == Material.POTION) {
                    Potion potion = Potion.fromItemStack(item);
                    if (potion.isSplash()) {
                        cancelInteraction = true;
                        player.sendMessage(ChatColor.RED + "You cannot throw potions in spawn.");
                    }
                } else if (item.getType() == Material.EGG || item.getType() == Material.SNOW_BALL) {
                    cancelInteraction = true;
                    player.sendMessage(ChatColor.RED + "You cannot throw projectiles in spawn.");
                }
            }

            Match match = plugin.getMatchHandler().getMatch(player);
            Kit kit = match != null && match.getKit() != null ? match.getKit() : playerData.getSelectedKit();
            if (kit != null && !cancelInteraction) {
                for (Ability ability : kit.getAbilities()) {
                    if (BrawlUtil.match(ability.getIcon(), event.getItem())) {
                        if (spawnProtection) {
                            player.sendMessage(ChatColor.RED + "You cannot use abilities in spawn.");
                            cancelInteraction = true;
                        }

                        if (!ability.bypassAbilityPreventZone() && RegionType.NO_ABILITY_ZONE.appliesTo(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "You cannot use abilities in this area.");
                            cancelInteraction = true;
                        }

                        if (!cancelInteraction) {
                            ability.onActivate(player);
                        }
                        break;
                    }
                }


                for (Killstreak ks : plugin.getKillstreakHandler().getKillstreaks().values()) {
                    if (ks != null && ks.isInteractable() && BrawlUtil.match(ks.getIcon(), event.getItem())) {
                        if (spawnProtection) {
                            player.sendMessage(ChatColor.RED + "You cannot use killstreaks in spawn.");
                            cancelInteraction = true;
                        }
                        if (!cancelInteraction) {
                            ks.onActivate(player, playerData);
                            if (event.getPlayer().getItemInHand().getAmount() > 1) {
                                event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                            } else {
                                event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                            }
                        }
                        break;
                    }
                }
            }

            if (cancelInteraction) {
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                player.updateInventory();
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

            if (!player.hasPlayedBefore()) {
                playerData.setKitPasses(1);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "We have given you " + ChatColor.YELLOW + "1x Free Kit Pass" + CC.LIGHT_PURPLE + " as this is your first time joining.");
            }

            if (playerData.getLastVoteRewards() < System.currentTimeMillis()) {
                if (hasVoted(player)) {
                    if (playerData.getLastVoteRewards() == -1L) { // first time voting
                        player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.YELLOW + "1x Free Kit Pass" + ChatColor.GREEN + " for voting on NameMC.");
                    } else { // reward every 2 days after voting
                        player.sendMessage(ChatColor.YELLOW + " + " + ChatColor.BOLD + 1 + ChatColor.YELLOW + " Kit Pass" + ChatColor.GRAY + " (Voting on NameMC)");
                    }

                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);
                    playerData.setKitPasses(playerData.getKitPasses() + 1);
                    playerData.setLastVoteRewards(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12));
                } else { // hasn't voted
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.GREEN + "Want a " + ChatColor.BOLD + "Free Kit Pass" + ChatColor.GREEN + " every 12 hours?");
                    player.sendMessage(ChatColor.GRAY + "Vote for us on " + ChatColor.WHITE + "https://namemc.com/server/kaze.gg" + ChatColor.GRAY + " for rewards.");
                    player.sendMessage(" ");
                }
            }


            plugin.getPlayerDataHandler().create(playerData, false);
            NametagHandler.reloadPlayer(player);
            VisibilityUtils.updateVisibility(player);
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
                player.setHealth(0.0);
            }
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            playerData.save();
            plugin.getPlayerDataHandler().remove(playerData);
        });
    }

    private static final String NOT_ENOUGH_POWER_METADATA = "Arrow_Punch";
    @EventHandler
    public void onArcher(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arrow projectile = (Arrow) event.getProjectile();
            if (projectile.getKnockbackStrength() >= 1 && event.getForce() < 0.90f) {
                projectile.setKnockbackStrength(0);
                if (!player.hasMetadata(NOT_ENOUGH_POWER_METADATA) || player.getMetadata(NOT_ENOUGH_POWER_METADATA, Brawl.getInstance()).asLong() < System.currentTimeMillis()) {
                    player.setMetadata(NOT_ENOUGH_POWER_METADATA, new FixedMetadataValue(Brawl.getInstance(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5)));
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Archer! " + ChatColor.GRAY + "Bow requires to be fully drawn for " + ChatColor.YELLOW + "Punch" + ChatColor.GRAY + " to be applied.");
                }
            }
        }
    }

    private boolean hasVoted(Player player) {
        try {
            Request request = new Request.Builder()
                    .url("https://api.namemc.com/server/kaze.gg/votes?profile=" + player.getUniqueId().toString())
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful() && response.code() == 200) {
                return Boolean.parseBoolean(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
