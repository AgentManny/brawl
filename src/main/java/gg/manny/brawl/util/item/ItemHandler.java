package gg.manny.brawl.util.item;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.kit.menu.KitSelectorMenu;
import gg.manny.brawl.util.HiddenStringUtils;
import gg.manny.brawl.util.item.type.InventoryType;
import gg.manny.brawl.util.item.type.MetadataType;
import gg.manny.pivot.util.PlayerUtils;
import gg.manny.pivot.util.file.type.BasicConfigurationFile;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        BasicConfigurationFile config = plugin.getMainConfig();
        ConfigurationSection configurationSection = config.getConfiguration().getConfigurationSection(path);
        ItemStack[] item = new ItemStack[36];

        if(configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                Material material = Material.matchMaterial(configurationSection.getString(key + ".TYPE"));


                int amount = 1;

                if(configurationSection.get(key + ".AMOUNT") != null) {
                    amount = configurationSection.getInt(key + ".AMOUNT", 1);
                }

                ItemBuilder itemBuilder = new ItemBuilder()
                        .material(material)
                        .amount(amount);

                String displayName = configurationSection.getString(key + ".NAME");
                if (displayName != null || displayName.isEmpty()) {
                    itemBuilder.name(CC.translate(displayName));
                }

                if(configurationSection.get(key + ".META") != null) {
                    itemBuilder.lore(Collections.singletonList(HiddenStringUtils.encodeString(configurationSection.getString(key + ".META"))));
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
        ItemBuilder builder = new ItemBuilder();

        builder.material(Material.matchMaterial(configurationSection.getString(key + ".TYPE")));

        if(configurationSection.get(key + ".AMOUNT") != null) {
            builder.amount(configurationSection.getInt(key + ".AMOUNT", 1));
        }

        if (configurationSection.get(key + ".DATA") != null) {
            builder.data((byte) configurationSection.getInt(key + ".DATA", 0));
        }

        if (configurationSection.get(key + ".NAME") != null) {
            builder.name(CC.translate(configurationSection.getString(key + ".NAME")));
        }

        if (configurationSection.get(key + ".LORE") != null) {
            builder.lore(configurationSection.getStringList(key + "LORE"));
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
        Player player = event.getPlayer();
        if(event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();

            if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                String metaData = HiddenStringUtils.extractHiddenString(lore.get(0));
                MetadataType metadataType = MetadataType.fromMetadata(metaData);
                switch (metadataType) {
                    case KIT_SELECTOR: {
                        new KitSelectorMenu(plugin).openMenu(player);
                        break;
                    }
                    case EVENT_SELECTOR: {
                        player.sendMessage(Locale.DISABLED.format());
                    }
                }
            }

        }
    }

    public void apply(Player player, ItemStack[] items) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        player.getInventory().setContents(items);
    }

    public void apply(Player player, InventoryType inventoryType) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        player.getInventory().setContents(this.getItems(inventoryType.getPath()));
    }


}
