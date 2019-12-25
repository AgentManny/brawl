package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.game.GameHandler;
import gg.manny.brawl.game.option.impl.StoreBlockOption;
import gg.manny.brawl.player.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class ProtectListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setDroppedExp(0);
            event.getDrops().clear();;
        }
    }

    @EventHandler
    public void onArrowStrike(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void preventMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerData pd = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());

        if (event.getClickedBlock() != null && !pd.isBuild()) {

            if (event.getItem() != null && (event.getItem().getType() == Material.CHEST || event.getItem().getType() == Material.TRAPPED_CHEST)) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }

            return;
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.REGEN)
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockPlaceEvent event) {
        PlayerData pd = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());
        if (!pd.isBuild()) {
            event.setBuild(false);
            event.setCancelled(true);
            return;
        }


    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        PlayerData pd = plugin.getPlayerDataHandler().getPlayerData((Player) event.getRemover());

        if (!pd.isBuild()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() == Material.SNOW_BLOCK) {
            recipe.getResult().setType(Material.AIR);
            return;
        }
        if (event.getView().getPlayer() instanceof Player) {
            Player player = (Player) event.getView().getPlayer();
            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.getFlags().contains(GameFlag.CRAFTING) && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) return;

            recipe.getResult().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) {
            return;
        }
        PlayerData pd = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());

        if (!pd.isBuild()) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        event.setCancelled(true);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        GameHandler gh = plugin.getGameHandler();
        if (gh.getActiveGame() != null && gh.getActiveGame().containsPlayer(player) && gh.getActiveGame().getGamePlayer(player).isAlive()){
            if (gh.getActiveGame().containsOption(StoreBlockOption.class)) {
                StoreBlockOption option = (StoreBlockOption) gh.getActiveGame().getOptions().get(StoreBlockOption.class);
                if (option.getAllowedBlocks().contains(event.getBlock().getType())) {
                    option.getData().put(event.getBlock().getLocation(), event.getBlock().getState());

                    event.getBlock().setType(Material.AIR);

                    if (!option.getPickable().isEmpty()) {
                        int range = ThreadLocalRandom.current().nextInt(1, option.getRandomRange());
                        option.getPickable().forEach(material -> player.getInventory().addItem(new ItemStack(material, range)));
                    }
                }
            }
        }

        if (!playerData.isBuild()) {
            event.setCancelled(true);
            event.setExpToDrop(0);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

}