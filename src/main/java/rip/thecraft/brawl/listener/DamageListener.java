package rip.thecraft.brawl.listener;

import gg.manny.streamline.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.ability.abilities.TimeLock;
import rip.thecraft.brawl.kit.ability.handlers.KillHandler;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.player.data.SpawnData;
import rip.thecraft.brawl.player.event.PlayerKillEvent;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.server.item.type.InventoryType;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.spawn.launchpad.LaunchpadHandler;
import rip.thecraft.brawl.spawn.levels.ExperienceType;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.falcon.profile.Profile;
import rip.thecraft.falcon.rank.Rank;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DamageListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.hasMetadata(TimeLock.TIMELOCK_METADATA)) {
            player.removeMetadata(TimeLock.TIMELOCK_METADATA, plugin);
        }
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
                            boolean changedItem = false;
                            List<String> lore = it.getItemMeta().hasLore() ? it.getItemMeta().getLore() : new ArrayList<>();
                            if (!(it.getType() == Material.BOWL || RefillType.isRefill(it))) {
                                lore.add(CC.DARK_GRAY + playerData.getSelectedKit().getName());
                            }
                            ItemStack toDrop = new ItemBuilder(it).lore(lore).create();
                            Item item = player.getWorld().dropItem(player.getLocation().add(Brawl.RANDOM.nextInt(2) - 1, 0, Brawl.RANDOM.nextInt(2) - 1), changedItem ? toDrop : it);
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

                Entity vehicle = player.getVehicle();
                if(vehicle != null){
                    if (vehicle.hasMetadata(LaunchpadHandler.JUMP_METADATA)) {
                        SpawnData spawnData = playerData.getSpawnData();
                        spawnData.cancelJump();
                    }
                    vehicle.remove();
                    player.eject();
                }

                player.teleport(plugin.getLocationByName("SPAWN"));
                playerData.setWarp(false);
                break;
            }
        }

        Player killer = player.getKiller();
        if(killer != null && !killer.getUniqueId().equals(player.getUniqueId())) {

            PlayerData killerData = plugin.getPlayerDataHandler().getPlayerData(killer);
            plugin.getServer().getPluginManager().callEvent(new PlayerKillEvent(killer, killerData.getPlayerState(), player)); // We only use this
            switch(killerData.getPlayerState()) {
                case FIGHTING: {
                    if (killerData.getPreviousKill() != null) {
                        if (player.getUniqueId() == killerData.getPreviousKill()) {
                            killerData.setKillTracker(killerData.getKillTracker() + 1);
                            if (killerData.getKillTracker() % 5 == 0) {
                                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                                    if (onlinePlayer.hasPermission(Rank.STAFF_NODE)) {
                                        Profile profile = Falcon.getInstance().getProfileHandler().getByPlayer(onlinePlayer);
                                        if (profile.getProfileData().isDoNotDisturb()) continue;
                                        onlinePlayer.sendMessage(ChatColor.DARK_PURPLE + "[Staff] " + ChatColor.WHITE + killer.getDisplayName() + ChatColor.GRAY + " might be boosting." + ChatColor.RED + " (" + killerData.getKillTracker() + " kills)");
                                    }
                                }
                            }
                            if (killerData.getKillTracker() >= 3) break; // Don't update statistics
                        } else {
                            killerData.setKillTracker(0);
                        }
                    }

                    if (killerData.getSelectedKit() != null) {
                        killerData.getSelectedKit().getAbilities().forEach(ability -> {
                            if (ability instanceof KillHandler) {
                                ((KillHandler) ability).onKill(killer, player);
                            }
                        });
                    }

                    killerData.getSpawnData().killed(player);

                    int killExp = ExperienceType.KILL.getExperience(); // You always get 5 exp per kill
                    if (killerData.getStatistic().get(StatisticType.KILLSTREAK) >= 15) {
                        int killstreakExpMultiplier = (int) ((int) (killerData.getStatistic().get(StatisticType.KILLSTREAK) / 15));
                        killExp += killstreakExpMultiplier;
                    }
                    killExp = Math.min(15, killExp);

                    killerData.getLevel().addExp(killer, killExp, ExperienceType.KILL, player.getDisplayName());
                    playerData.getSpawnData().applyAssists(killer, playerData.getSpawnData().getWorth());

                    killerData.setPreviousKill(player.getUniqueId());

                    player.sendMessage(ChatColor.RED + "You have been killed by " + CC.WHITE + killer.getDisplayName() + CC.RED + " [" + (Math.round((killer.getHealth() * 10) / 2) / 10) + "\u2764] using " + CC.WHITE + (killerData.getSelectedKit() == null ? "None" : killerData.getSelectedKit().getName()) + CC.RED + " kit.");
                    break;
                }
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 5L);

        player.setVelocity(new Vector(0, 0, 0));
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

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
                Game activeGame = Brawl.getInstance().getGameHandler().getActiveGame();
                if (activeGame.getFlags().contains(GameFlag.PLAYER_ELIMINATE)) {
                    Location location = activeGame.getLocationByName("Lobby");
                    event.setRespawnLocation(location != null ? location : playerData.getLastLocation());
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


    private static final double ARMOR_PROBABILITY = 0.30;
    private static final double OTHER_PROBABILITY = 0.75;

    private boolean shouldFilter(ItemStack itemStack) {
        double random = Math.random();
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
            case LEATHER_BOOTS: {
                return random <= ARMOR_PROBABILITY;
            }
            default: {
                if (RefillType.isRefill(itemStack)) {
                    return random <= OTHER_PROBABILITY;
                }
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

            if(RegionType.SAFEZONE.appliesTo(player.getLocation())){
                e.setCancelled(true);
                return;
            }

            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (game.getState() == GameState.STARTED && game.getFlags().contains(GameFlag.FALL_ELIMINATE)) {
                        game.handleElimination(player, player.getLocation(), GameElimination.FALL);
                        e.setCancelled(true);
                    }

                    if (game.getFlags().contains(GameFlag.NO_FALL)) {
                        e.setCancelled(true);
                    }
                }
            }

            Player damager = null;
            if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                e.setDamage(e.getDamage() < 2.5 ? 2.5 : e.getDamage() / 1.25);
            }

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
            if (!e.isCancelled()) {
                if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                    playerData.getSpawnData().damagedBy(damager, e.getDamage());
                }
            }
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
