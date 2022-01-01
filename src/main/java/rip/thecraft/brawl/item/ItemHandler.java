package rip.thecraft.brawl.item;

import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.item.type.MetadataType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.HiddenStringUtils;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHandler implements Listener {

    private final Brawl plugin;

    public ItemHandler(Brawl plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ItemStack[] getItems(String path) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection(path);
        ItemStack[] item = new ItemStack[36];

        if(configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                Material material = Material.matchMaterial(configurationSection.getString(key + ".TYPE"));


                int amount = 1;
                int data = 0;
                if(configurationSection.get(key + ".AMOUNT") != null) {
                    amount = configurationSection.getInt(key + ".AMOUNT", 1);
                }

                if(configurationSection.get(key + ".DATA") != null) {
                    data = configurationSection.getInt(key + ".DATA", 0);
                }

                ItemBuilder itemBuilder = new ItemBuilder(material)
                        .data((short) data)
                        .amount(amount);

                String displayName = configurationSection.getString(key + ".NAME");
                if (displayName != null || displayName.isEmpty()) {
                    itemBuilder.name(CC.translate(displayName));
                }

                if(configurationSection.get(key + ".META") != null) {
                    String metaData = configurationSection.getString(key + ".META");
                    if (!MetadataType.isMetadata(metaData)) {
                        plugin.getLogger().severe("Item " + material.name() + " encoded " + metaData + " but doesn't exist");
                    }
                    itemBuilder.lore(Collections.singletonList(HiddenStringUtils.encodeString(metaData)));
                }

                int i = 0;
                try {
                    i = Integer.parseInt(key);
                } catch (NumberFormatException ignored) {

                } finally {
                    ItemStack itemStack = itemBuilder.create();
                    item[i] = itemStack;

                }

            }
        }
        return item;
    }

    public ItemStack toItemStack(String key, ConfigurationSection configurationSection) {
        if (configurationSection == null) {
            return new ItemStack(Material.AIR);
        }
        Material material = Material.matchMaterial(configurationSection.getString(key + ".TYPE"));
        ItemBuilder builder = new ItemBuilder(material)
                .data((byte) configurationSection.getInt(key + ".DATA", 0))
                .amount(configurationSection.getInt(key + ".AMOUNT", 1));

        String displayName = configurationSection.getString(key + ".NAME");
        if (displayName != null || displayName.isEmpty()) {
            builder.name(CC.translate(displayName));
        }

        if(configurationSection.get(key + ".META") != null) {
            String metaData = configurationSection.getString(key + ".META");
            if (!MetadataType.isMetadata(metaData)) {
                plugin.getLogger().severe("Item " + material.name() + " encoded " + metaData + " but doesn't exist");
            }
            builder.lore(Collections.singletonList(HiddenStringUtils.encodeString(metaData)));
        }

        if (configurationSection.get(key + ".LORE") != null) {
            builder.lore(configurationSection.getStringList(key + ".LORE"));
        }


        if (configurationSection.get(key + ".ENCHANTMENTS") != null) {
            Map<Enchantment, Integer> enchantmentMap = new HashMap<>();
            ConfigurationSection enchantmentConfigurationSection = configurationSection.getConfigurationSection(key + ".ENCHANTMENTS");
            for (String enchantKey : enchantmentConfigurationSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantKey);
                Integer value = enchantmentConfigurationSection.getInt(enchantKey, 1);
                if (enchantment != null) {
                    enchantmentMap.put(enchantment, value);
                }
            }  enchantmentMap.forEach(builder::enchant);

        }

        return builder.create();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            Player player = event.getPlayer();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                String metaData = HiddenStringUtils.extractHiddenString(lore.get(0));
                if (MetadataType.isMetadata(metaData)) {
                    MetadataType metadataType = MetadataType.fromMetadata(metaData);
                    if (metadataType.isCancellable()) {
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setUseItemInHand(Event.Result.DENY);
                    }

                    if (metadataType.getActivate() != null) {
                        metadataType.getActivate().accept(player, playerData);
                        return;
                    }

                    if (metadataType == MetadataType.DUEL_ARENA_DUEL) return; // This uses PlayerInteractEntityEvent

                    switch (metadataType) {
                        case EVENT_VOTE: {
                            if (playerData.getLastAction() > System.currentTimeMillis()) {
                                player.sendMessage(ChatColor.RED + "Please wait before doing this again.");
                                return;
                            }

                            GameLobby lobby = plugin.getGameHandler().getLobby();
                            if (lobby != null && lobby.getPlayers().contains(player.getUniqueId())) {
                                String name = CC.strip(meta.getDisplayName().split(" ")[1]);
                                if (lobby.getVoteMap().containsKey(name)) {
                                    lobby.removeVote(player.getUniqueId());
                                    lobby.getVoteMap().get(name).add(player.getUniqueId());

                                    player.sendMessage(ChatColor.GREEN + "Voted for " + ChatColor.LIGHT_PURPLE + name + ChatColor.GREEN + " map on " + lobby.getGameType().getName() + ".");
                                    lobby.updateVotes();
                                    player.updateInventory();
                                    playerData.setLastAction(System.currentTimeMillis() + 250L);
                                }
                            }
                            break;
                        }

                        default: {
                            player.sendMessage(ChatColor.RED + "This feature has been temporarily disabled. Please try again later.");
                        }
                    }
                }
            }

        }
    }

    public void apply(Player player, ItemStack[] items) {
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);
        player.getInventory().setContents(items);
        if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory) {
            player.getOpenInventory().getTopInventory().clear();
        }
        player.updateInventory();
        player.closeInventory();
    }

    public void apply(Player player, InventoryType inventoryType) {
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);
        if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory) {
            player.getOpenInventory().getTopInventory().clear();
        }
        player.getInventory().setContents(this.getItems(inventoryType.getPath()));
        player.updateInventory();
        player.closeInventory();

    }


}
