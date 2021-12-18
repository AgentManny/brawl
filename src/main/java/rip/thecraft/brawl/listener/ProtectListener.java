package rip.thecraft.brawl.listener;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupExperienceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.games.Feast;
import rip.thecraft.brawl.game.option.impl.StoreBlockOption;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.region.RegionType;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class ProtectListener implements Listener {

    private final Brawl plugin;

    private static final ImmutableMultimap<Material, Material> ITEM_ON_BLOCK_RIGHT_CLICK_DENY = ImmutableMultimap.<Material, Material>builder().
            put(Material.DIAMOND_HOE, Material.GRASS).
            put(Material.GOLD_HOE, Material.GRASS).
            put(Material.IRON_HOE, Material.GRASS).
            put(Material.STONE_HOE, Material.GRASS).
            put(Material.WOOD_HOE, Material.GRASS).
            build();

    // List of materials a player can not right click in protected territory
    private static final ImmutableSet<Material> BLOCK_RIGHT_CLICK_DENY = Sets.immutableEnumSet(
            Material.BED,
            Material.BED_BLOCK,
            Material.BEACON,
            Material.FENCE_GATE,
            Material.IRON_DOOR,
            Material.TRAP_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.BREWING_STAND,
            Material.HOPPER,
            Material.DROPPER,
            Material.DISPENSER,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.ENCHANTMENT_TABLE,
            Material.WORKBENCH,
            Material.ANVIL,
            Material.LEVER,
            Material.FIRE,
            Material.NOTE_BLOCK,
            Material.JUKEBOX
    );

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

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
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onArrowStrike(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        if (entity instanceof Arrow) {
            plugin.getServer().getScheduler().runTaskLater(Brawl.getInstance(), entity::remove, 30L);
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
        Player player = event.getPlayer();

//        if (event.getClickedBlock() != null && !player.hasMetadata("build")) {
//            if (event.getItem() != null && (event.getItem().getType() == Material.CHEST || event.getItem().getType() == Material.TRAPPED_CHEST)) {
//                event.setUseInteractedBlock(Event.Result.DENY);
//                event.setUseItemInHand(Event.Result.DENY);
//                event.setCancelled(true);
//                player.updateInventory();
//            }
//        }

        if (!event.hasBlock()) return;

        Block block = event.getClickedBlock();
        Action action = event.getAction();
        if (RegionType.SAFEZONE.appliesTo(player.getLocation())) {
            if (event.getItem() != null && event.hasItem() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { // Prevent interacting with certain items in spawn
                if (event.getItem().getType() != null && event.getItem().getType() == Material.FISHING_ROD) {
                    event.setCancelled(true);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setUseItemInHand(Event.Result.DENY);
                    return;
                }
            }
        }

        if (action == Action.PHYSICAL) { // Prevent players from trampling on crops or any other pressure plates besides stone, etc.
            if (!player.hasMetadata("build") && (event.hasBlock() && event.getClickedBlock().getType() != Material.STONE_PLATE)) {
                event.setCancelled(true);
            }
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean canRightClick;
            MaterialData blockData;
            Material blockType = block.getType();

            if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
                Game game = Brawl.getInstance().getGameHandler().getActiveGame();
                if (game != null && game.containsPlayer(player)) {
                    GamePlayer gamePlayer = game.getGamePlayer(player);
                    if(game instanceof Feast && game.getState() == GameState.STARTED){
                        if (gamePlayer.isAlive()) {
                            event.setUseInteractedBlock(Event.Result.ALLOW);
                            event.setUseItemInHand(Event.Result.ALLOW);
                            event.setCancelled(false);
                            player.updateInventory();
                            return;
                        }
                    }
                }
            }

            // Firstly, check if this block is not on the explicit blacklist.
            canRightClick = !BLOCK_RIGHT_CLICK_DENY.contains(blockType);
            if (canRightClick) {
                Material itemType = event.hasItem() ? event.getItem().getType() : null;
                if (Material.EYE_OF_ENDER == itemType && Material.ENDER_PORTAL_FRAME == blockType && block.getData() != 4) {
                    // If the player is right clicking an Ender Portal Frame with an Ender Portal Eye and it is empty.
                    canRightClick = false;

                } else if (Material.GLASS_BOTTLE == itemType && (blockData = block.getState().getData()) instanceof Cauldron && !((Cauldron) blockData).isEmpty()) {
                    // If the player is right clicking a Cauldron that contains liquid with a Glass Bottle.
                    canRightClick = false;

                } else if (itemType != null && ITEM_ON_BLOCK_RIGHT_CLICK_DENY.get(itemType).contains(block.getType())) {
                    // Finally, check if this block is not blacklisted with the item the player right clicked it with.
                    canRightClick = false;
                }
            }
            if (!canRightClick) {
                if (player.hasMetadata("build")) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.REGEN)
            e.setCancelled(true);
    }



    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        if (!event.getRemover().hasMetadata("build")) {
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
    public void onCraftFinish(CraftItemEvent event){
        if(event.getWhoClicked() instanceof Player){
            Player player = (Player) event.getWhoClicked();

            Game game = plugin.getGameHandler().getActiveGame();
            if(game != null && game.getFlags().contains(GameFlag.CRAFTING) && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) return;

            event.setCancelled(true);
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

        if (!event.getPlayer().hasMetadata("build")) {
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
    public void onPlayerExpPickup(PlayerPickupExperienceEvent event) {
        event.setCancelled(true); // We use experience as a visual effect
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        GameHandler gh = plugin.getGameHandler();
        if (gh.getActiveGame() != null && gh.getActiveGame().containsPlayer(player) && gh.getActiveGame().getGamePlayer(player).isAlive()){
            if (gh.getActiveGame().containsOption(StoreBlockOption.class)) {
                StoreBlockOption option = (StoreBlockOption) gh.getActiveGame().getOptions().get(StoreBlockOption.class);
                if (option.getAllowedBlocks().contains(event.getBlockPlaced().getType())) {
                    option.getData().put(event.getBlockReplacedState().getLocation(), event.getBlockReplacedState());

                    event.setBuild(true);
                    event.setCancelled(false);
                    return;
                }
            }
        }

        if (!event.getPlayer().hasMetadata("build")) {
            event.setBuild(false);
            event.setCancelled(true);
            return;
        }
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
                    if(option.getData().containsKey(event.getBlock().getLocation())){
                        event.getBlock().setType(Material.AIR);

                        if (!option.getPickable().isEmpty()) {
                            int range = ThreadLocalRandom.current().nextInt(1, option.getRandomRange());
                            option.getPickable().forEach(material -> player.getInventory().addItem(new ItemStack(material, range)));
                        }
                        return;
                    }

                    option.getData().put(event.getBlock().getLocation(), event.getBlock().getState());

                    event.getBlock().setType(Material.AIR);

                    if (!option.getPickable().isEmpty()) {
                        int range = ThreadLocalRandom.current().nextInt(1, option.getRandomRange());
                        option.getPickable().forEach(material -> player.getInventory().addItem(new ItemStack(material, range)));
                    }
                }
            }
        }

        if (!player.hasMetadata("build")) {
            event.setCancelled(true);
            event.setExpToDrop(0);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter()instanceof Player) {
            Player player = ((Player) event.getEntity().getShooter());
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection()) {
                player.sendMessage(ChatColor.RED + "You cannot throw projectiles in spawn.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection()) {
                player.sendMessage(ChatColor.RED + "You cannot use that here.");
                event.setCancelled(true);
            }
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