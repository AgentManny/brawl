package rip.thecraft.brawl.listener;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.spawn.perks.Perk;
import rip.thecraft.brawl.util.SchedulerUtil;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class SoupListener implements Listener {

    private final Brawl plugin;

    public static final String REFILL_METADATA = "REFILL_DATA";
    public static final String PLAYER_REFILLING_METADATA = "PLAYER_REFILLING";
    private final Set<BlockState> refillStations = new HashSet<>();

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() instanceof Brawl) {
            for (BlockState state : refillStations) {
                Location loc = state.getLocation();
                state.setMaterial(Material.EMERALD_BLOCK);
                System.out.println("[Refill Station] Reverted (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") back to it's original state.");

            }
        }
    }

    @EventHandler
    public void onSign(SignChangeEvent event) {
        if (event.getLine(0).contains("Refill") && event.getPlayer().isOp()) {
            event.setLine(0, " ");
            event.setLine(1, CC.DARK_PURPLE + "- Refill -");
            event.setLine(2, "Soup/Potions");
            event.setLine(3, " ");

            Sign sign = (Sign) event.getBlock().getState();
            MaterialData materialData = sign.getMaterialData();
            Block signBlock = sign.getBlock().getRelative(materialData instanceof Attachable ? ((Attachable) materialData).getAttachedFace() : BlockFace.DOWN);
            signBlock.setType(Material.EMERALD_BLOCK);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Added a new refill station at " + ChatColor.WHITE + "(" + signBlock.getLocation().getBlockX() + ", " + signBlock.getLocation().getBlockY() + ", " + signBlock.getLocation().getBlockZ() + ")" + ChatColor.GREEN + ".");

            for (BlockFace face : BlockFace.values()) {
                Block newSign = signBlock.getRelative(face);
                if (newSign.getType() == Material.WALL_SIGN) {
                    Sign sign2 = (Sign) newSign.getState();
                    if (!sign2.getLine(1).contains("Refill")) {
                        sign2.setLine(0, " ");
                        sign2.setLine(1, CC.DARK_PURPLE + "- Refill -");
                        sign2.setLine(2, "Soup/Potions");
                        sign2.setLine(3, " ");
                        sign2.update();
                        event.getPlayer().sendMessage(ChatColor.GRAY + "* Found a nearby sign (" + face.name() + ") which was added as a Soup sign");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            Game game = plugin.getGameHandler().getActiveGame();
            if (game != null && game.getFlags().contains(GameFlag.HUNGER) && game.containsPlayer(player) && game.getGamePlayer(player).isAlive()) return;

            event.setCancelled(true);
            event.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Player player = event.getPlayer();
            PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

            double health = event.getPlayer().getHealth();

            double maxHealth = event.getPlayer().getMaxHealth();
            if (event.hasItem() && event.getItem().getTypeId() == 282 && health < maxHealth) {
                event.setCancelled(true);
                player.setHealth(health + 7 > maxHealth ? maxHealth : health + 7);

                if(data.usingPerk(Perk.QUICKDROP)){
                    player.setItemInHand(new ItemStack(Material.AIR));
                }else{
                    player.getItemInHand().setType(Material.BOWL);
                }
                player.updateInventory();
            } else if (event.hasItem() && event.getItem().getTypeId() == 282 && event.getPlayer().getFoodLevel() < 20) {
                event.setCancelled(true);
                player.setFoodLevel((player.getFoodLevel() + 7) > 20D ? 20 : player.getFoodLevel() + 7);
                player.getItemInHand().setType(Material.BOWL);
            } else if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
                Block clickedBlock = event.getClickedBlock();
                Sign sign = (Sign) clickedBlock.getState();
                MaterialData materialData = sign.getMaterialData();
                if(sign.getLine(1).contains("Refill")) {
                    Block signBlock = sign.getBlock().getRelative(materialData instanceof Attachable ? ((Attachable) materialData).getAttachedFace() : BlockFace.DOWN);
                    handleStation(player, signBlock);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = event.getActor();
        Inventory inventory = event.getInventory();
        if (inventory != null && inventory.getTitle().startsWith("Refill") && player.hasMetadata(PLAYER_REFILLING_METADATA)) {
            Location location = (Location) player.getMetadata(PLAYER_REFILLING_METADATA, plugin).value();

            location.getBlock().removeMetadata(PLAYER_REFILLING_METADATA, plugin);

            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle().playerConnection.player;
            if (entityPlayer.isBlocking()) {
                entityPlayer.bw();
            }
            player.removeMetadata(PLAYER_REFILLING_METADATA, plugin);

            //player.getInventory().firstEmpty() != -1
            if (inventory.firstEmpty() != -1) { // Contains an empty soup which means it was used
                double emptySlots = 0;
                double totalSlots = inventory.getSize();
                for (ItemStack content : inventory.getContents()) {
                    if (content == null || content.getType() == Material.AIR || content.getType() == Material.BOWL) {
                        emptySlots++;
                    }
                }

                double percentageSlots = (emptySlots / totalSlots) * 100;
                // Every X seconds refill a soup
               // Bukkit.broadcastMessage("Percentage used: " + percentageSlots + ". Should refill in " + Math.round(percentageSlots / 5) + " seconds");
                replenishStation(location.getBlock(), (int) Math.round(percentageSlots / 5));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(PLAYER_REFILLING_METADATA)) { // If they quit while using a soup we don't want it messing that station up
            Location location = (Location) player.getMetadata(PLAYER_REFILLING_METADATA, plugin).value();
            location.getBlock().removeMetadata(PLAYER_REFILLING_METADATA, plugin);
        }
    }

    public void handleStation(Player player, Block attachedBlock) {
        BlockState state = attachedBlock.getState();
        if (!attachedBlock.hasMetadata(REFILL_METADATA) && !this.refillStations.contains(state)) { // Metadata is persistent, if the server crashes soup signs won't revert back to their original state.
            this.refillStations.add(attachedBlock.getState()); // Ensure it's added
            attachedBlock.setMetadata(REFILL_METADATA, new FixedMetadataValue(plugin, System.currentTimeMillis()));
            attachedBlock.setType(Material.EMERALD_BLOCK);
        }

        if (attachedBlock.getType() == Material.REDSTONE_BLOCK) {
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "REFILL STATION! " + ChatColor.GRAY + "This station is still refilling.");
            return;
        }

//        ItemStack item = player.getItemInHand();
//        if (item != null && item.getType() != null && item.getType().name().contains("_SWORD")) {
//            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "REFILL STATION! " + ChatColor.GRAY + "You can't open a station with a sword.");
//            return;
//        }

        if (attachedBlock.hasMetadata(PLAYER_REFILLING_METADATA)) {
            Player usingPlayer = Bukkit.getPlayer(UUID.fromString(attachedBlock.getMetadata(PLAYER_REFILLING_METADATA, plugin).asString()));
            if (usingPlayer == null) {
                attachedBlock.removeMetadata(PLAYER_REFILLING_METADATA, plugin);
            } else {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "REFILL STATION! " + ChatColor.GRAY + "This station is currently occupied, another player is using it.");
                return;
            }
        }

        // Assume it's an emerald block if not, it'll automatically set it when the replenish task is over
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        RefillType refillType = playerData.getRefillType();

        int inventorySize = playerData.usingPerk(Perk.SCAVENGER) ? 36: 18;

        attachedBlock.setMetadata(PLAYER_REFILLING_METADATA, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        player.setMetadata(PLAYER_REFILLING_METADATA, new FixedMetadataValue(plugin, attachedBlock.getLocation()));

        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Refill your " + refillType.name().toLowerCase() + "s...");
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, refillType.getItem());
        }
        player.openInventory(inventory);
    }

    public void replenishStation(Block attachedBlock, int replenishTime) {
        this.refillStations.add(attachedBlock.getState()); // Ensure it's added

        attachedBlock.setMetadata(REFILL_METADATA, new FixedMetadataValue(plugin, System.currentTimeMillis()));
        attachedBlock.setType(Material.REDSTONE_BLOCK);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                attachedBlock.setType(Material.EMERALD_BLOCK);

                if (attachedBlock.hasMetadata(PLAYER_REFILLING_METADATA)) {
                    Player usingPlayer = Bukkit.getPlayer(UUID.fromString(attachedBlock.getMetadata(PLAYER_REFILLING_METADATA, plugin).asString()));
                    if (usingPlayer != null) {
                        usingPlayer.removeMetadata(PLAYER_REFILLING_METADATA, plugin);
                    }
                    attachedBlock.removeMetadata(PLAYER_REFILLING_METADATA, plugin);
                }


        }, replenishTime * 20);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null) return;

        Material type = item.getType();
        switch(type) {
            case BOWL:
            case GLASS_BOTTLE:{
                SchedulerUtil.runTaskLater(() -> event.getItemDrop().remove(), 5L, false);
                break;
            }
            default: {
                if (RefillType.isRefill(item)) {
                    PlayerData data = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());
                    if(data.getPlayerState() == PlayerState.MATCH || data.isDuelArena()){
                        event.getItemDrop().remove();
                        return;
                    }

                    SchedulerUtil.runTaskLater(() -> event.getItemDrop().remove(), RegionType.SAFEZONE.appliesTo(event.getPlayer().getLocation()) ? 5L : 100L, false);
                    return;
                }

                if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(event.getPlayer());
                    if (playerData.getSelectedKit() != null) {
                        List<String> lore = item.getItemMeta().getLore();
                        boolean canDrop = lore.contains(ChatColor.DARK_GRAY + playerData.getSelectedKit().getName());
                        if (canDrop) {
                            SchedulerUtil.runTaskLater(() -> event.getItemDrop().remove(), 5L, false);
                            return;
                        }
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        if (item == null) return;

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        RefillType refillType = RefillType.getType(item);
        RefillType playerRefill = playerData.getRefillType();

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            boolean canPickup = refillType != null || lore.contains(ChatColor.DARK_GRAY + playerData.getSelectedKit().getName());
            if (!canPickup) {
                event.setCancelled(true);
                return;
            }
        } else if (refillType != playerRefill) { // Change healing to player's refill type
            if (playerRefill.getItem() != null) {
                event.getItem().setItemStack(playerRefill.getItem());
                event.setCancelled(true);
            }
        }
    }
}
