package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.brawl.util.item.type.InventoryType;
import gg.manny.pivot.util.PivotUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

@RequiredArgsConstructor
public class DamageListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        switch(playerData.getPlayerState()) {
            case SPAWN: {
                player.setHealth(20.0D);
                break;
            }
            case FIGHTING: {
                int i = 0;
                for (ItemStack it : event.getDrops()) {
                    if (this.shouldFilter(it)) {
                        Item item = player.getWorld().dropItem(player.getLocation().add(Brawl.RANDOM.nextInt(2) - 1, 0, Brawl.RANDOM.nextInt(2) - 1), it);
                        plugin.getServer().getScheduler().runTaskLater(plugin, item::remove, 5L + (4 * i++));
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

                break;
            }
        }

        Player killer = player.getKiller();
        if(killer != null) {
            PlayerData killerData = plugin.getPlayerDataHandler().getPlayerData(killer);

            switch(killerData.getPlayerState()) {
                case FIGHTING: {

                    PlayerStatistic statistic = killerData.getStatistic();
                    statistic.add(StatisticType.KILLS);
                    statistic.add(StatisticType.KILLSTREAK);

                    Kit selectedKit = playerData.getSelectedKit();
                    if (selectedKit != null) {
                        statistic.get(selectedKit).addKills();
                    }
                    break;
                }
            }

        }

        player.setVelocity(new Vector(0, 0, 0));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if(player.isDead()) {
                player.spigot().respawn();
            }
        }, 2L);

        event.setDeathMessage(null);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        switch(playerData.getPlayerState()) {
            default: {
                plugin.getItemHandler().apply(player, InventoryType.SPAWN);
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
                return Brawl.RANDOM.nextBoolean();
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

            Player damager = PivotUtil.getDamager(event);
            if(damager != null) {
                if(damager == player) return;

                PlayerData damagerData = plugin.getPlayerDataHandler().getPlayerData(damager);

                if (damagerData.isSpawnProtection()) {
                    damager.sendMessage(Locale.PLAYER_PROTECTION_DAMAGE.format());
                    event.setCancelled(true);
                    return;
                }

                if(playerData.isSpawnProtection()) {
                    event.setCancelled(true);
                    damager.sendMessage(Locale.PLAYER_PROTECTION_DAMAGE_OTHER.format(player.getDisplayName()));
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent entity) {
        if (entity.getEntity() instanceof Player) {
            Player player = (Player) entity.getEntity();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection()) {
                entity.setCancelled(true);
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
                            player.sendMessage(Locale.PLAYER_PROTECTION_DAMAGE_OTHER.format(playerEntity.getDisplayName()));
                        }
                    }
                }
            }
        }
    }

}
