package rip.thecraft.brawl.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DamageListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        event.setDroppedExp(0);
        playerData.setLastLocation(player.getLocation());
        switch(playerData.getPlayerState()) {
            case GAME: {
                Game game = Brawl.getInstance().getGameHandler().getActiveGame();
                game.handleElimination(player, player.getLocation(), player.getKiller() != null ? GameElimination.PLAYER : GameElimination.DEATH);
                break;
            }
            case MATCH: {
                plugin.getMatchHandler().getMatch(player).eliminated(player);
                break;
            }
            case SPAWN: {
                player.setHealth(20.0D);
                break;
            }
            case FIGHTING: {
                int i = 0;
                if (!RegionType.SAFEZONE.appliesTo(event.getEntity().getLocation()) && playerData.getSelectedKit() != null) {
                    for (ItemStack it : event.getDrops()) {
                        if (this.shouldFilter(it)) {
                            List<String> lore = it.getItemMeta().hasLore() ? it.getItemMeta().getLore() : new ArrayList<>();
                            if (!(it.getType() == Material.MUSHROOM_SOUP || it.getType() == Material.BOWL)) {
                                lore.add(ChatColor.GRAY + "PvP Loot");
                                lore.add(CC.DARK_GRAY + playerData.getSelectedKit().getName());
                            }

                            ItemStack toDrop = new ItemBuilder(it).lore(lore).create();
                            Item item = player.getWorld().dropItem(player.getLocation().add(Brawl.RANDOM.nextInt(2) - 1, 0, Brawl.RANDOM.nextInt(2) - 1), toDrop);
                            plugin.getServer().getScheduler().runTaskLater(plugin, item::remove, 15L + (4 * i++));
                        }
                    }
                }
                PlayerStatistic statistic = playerData.getStatistic();


                statistic.add(StatisticType.DEATHS);
                statistic.set(StatisticType.KILLSTREAK, 0.0D);

                Kit selectedKit = playerData.getSelectedKit();
                if (selectedKit != null) {
                    statistic.get(selectedKit).addDeaths();

                    playerData.setPreviousKit(selectedKit);
                    playerData.setSelectedKit(null);
                }
                player.teleport(plugin.getLocationByName("SPAWN"));
                break;
            }
        }

        Player killer = player.getKiller();
        if(killer != null && killer != player) {
            PlayerData killerData = plugin.getPlayerDataHandler().getPlayerData(killer);

            switch(killerData.getPlayerState()) {
                case FIGHTING: {

                    if (killerData.getPreviousKill() != null) {
                        if (player.getUniqueId() == killerData.getPreviousKill()) {
                            killerData.setKillTracker(killerData.getKillTracker() + 1);
                            if (killerData.getKillTracker() >= 3) {
                                killer.sendMessage(CC.RED + CC.BOLD + "Boosting! " + CC.YELLOW + "Your statistics aren't being updated.");
                                break;
                            }
                        } else {
                            killerData.setKillTracker(0);
                        }
                    }

                    if (killerData.getSelectedKit() != null) {
                        killerData.getSelectedKit().getAbilities().forEach(ability -> ability.onKill(killer));
                    }

                    killerData.getSpawnData().killed(player);

                    int killExp = 5; // You always get 5 exp per kill
                    if (killerData.getStatistic().get(StatisticType.KILLSTREAK) > 5) {
                        killExp += (int) Math.min(50, (killerData.getStatistic().get(StatisticType.KILLSTREAK) * 0.75)); // Killstreak multiplier only takes effect after 5 kills
                    }

                    killerData.getLevel().addExp(killer, killExp, "Killed " + player.getDisplayName());
                    playerData.getSpawnData().applyAssists(killer, playerData.getSpawnData().getWorth());

                    killerData.setPreviousKill(player.getUniqueId());

                    player.sendMessage(ChatColor.RED + "You have been killed by " + CC.WHITE + killer.getDisplayName() + CC.RED + " [" + (Math.round((killer.getHealth() * 10) / 2) / 10) + "\u2764] using " + CC.WHITE + (killerData.getSelectedKit() == null ? "None" : killerData.getSelectedKit().getName()) + CC.RED + " kit.");
                    break;
                }
            }

        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 4L);

        player.setVelocity(new Vector(0, 0, 0));
        event.setDeathMessage(null);
        event.getDrops().clear();}

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        playerData.getSpawnData().getDamageReceived().clear();
        playerData.setCombatTaggedTil(-1);
        player.setFireTicks(0);
        player.setVelocity(new Vector());
        switch(playerData.getPlayerState()) {
            case MATCH: {
                event.setRespawnLocation(playerData.getLastLocation() != null ? playerData.getLastLocation().add(0, 1, 0) :  plugin.getMatchHandler().getMatch(player).getArena().getLocations()[0]);
                break;
            }
            case GAME: {
                if (Brawl.getInstance().getGameHandler().getActiveGame().getFlags().contains(GameFlag.PLAYER_ELIMINATE)) {
                    event.setRespawnLocation(playerData.getLastLocation());
                    Brawl.getInstance().getSpectatorManager().addSpectator(player, Brawl.getInstance().getGameHandler().getActiveGame());
                }
                break;
            }
            case ARENA: {
                plugin.getItemHandler().apply(player, InventoryType.ARENA);
                playerData.setSpawnProtection(false);
                playerData.setDuelArena(true);
                event.setRespawnLocation(plugin.getLocationByName("DUEL_ARENA"));
                break;
            }
            default: {
                plugin.getItemHandler().apply(player, InventoryType.SPAWN);
                playerData.setSpawnProtection(true);
                event.setRespawnLocation(plugin.getLocationByName("SPAWN"));
                break;
            }
        }

    }



    private boolean shouldFilter(ItemStack itemStack) {
        switch(itemStack.getType()) {
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
            case IRON_HELMET:
            case IRON_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_BOOTS:
            case GOLD_HELMET:
            case GOLD_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_BOOTS:
            case BOWL:
            case MUSHROOM_SOUP:
                return Math.random() * 100 < 70;
            default: {
                return false;
            }
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;


        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            if (playerData.isWarping()) {
                playerData.cancelWarp();
            }

            Player damager = PlayerUtils.getDamager(event);
            if(damager != null) {
                if(damager == player) return;

                PlayerData damagerData = plugin.getPlayerDataHandler().getPlayerData(damager);

                if (playerData.isDuelArena() || damagerData.isDuelArena()) {
                    event.setCancelled(true);
                }

                if (damagerData.isSpawnProtection()) {
                    damager.sendMessage(ChatColor.RED + "You still have spawn protection.");
                    event.setCancelled(true);
                    return;
                }

                if(playerData.isSpawnProtection()) {
                    event.setCancelled(true);
                    damager.sendMessage(player.getDisplayName() + ChatColor.RED + " still has spawn protection.");
                }

                if (damagerData.getPlayerState() == PlayerState.GAME_LOBBY || playerData.getPlayerState() == PlayerState.GAME_LOBBY) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().hasMetadata("NPC")) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection() || playerData.isDuelArena() || playerData.getPlayerState() == PlayerState.GAME_LOBBY) {
                e.setCancelled(true);
            } else if (playerData.isNoFallDamage() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
                playerData.setNoFallDamage(false);
            }

            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) {
                if (game.getFlags().contains(GameFlag.NO_FALL) && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
            }

            Player damager = null;

            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();

                } else if (event.getDamager() instanceof Projectile) {
                    if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                        return;
                    }
                    damager = ((Player) ((Projectile) event.getDamager()).getShooter());
                }

                if (damager == player) {
                    return;
                }

            }
            playerData.getSpawnData().damagedBy(damager, e.getDamage());
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ProjectileSource projectileSource = event.getPotion().getShooter();
        if (projectileSource instanceof Player) {
            Player player = (Player) projectileSource;
            PlayerData shooterData = plugin.getPlayerDataHandler().getPlayerData(player);

            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    Player playerEntity = (Player) entity;
                    PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(playerEntity);
                    if (!playerData.isSpawnProtection()) {
                        if (shooterData.isSpawnProtection()) {
                            event.setCancelled(true);
                            event.setIntensity(entity, 0.0D);
                            player.sendMessage(player.getDisplayName() + ChatColor.RED + " still has spawn protection.");
                        }
                    }
                }
            }
        }
    }

}
