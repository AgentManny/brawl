package gg.manny.brawl.market.item.impl;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.market.item.MarketPurchasableItem;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.util.inventory.ItemUtil;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.v1_7_R4.LocaleI18n;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ShopEnchantment implements MarketPurchasableItem {
    private Enchantment enchantment;
    private int level;

    private int price;

    public String getFriendlyName(boolean colored) {
        Enchantment nmsE = Enchantment.getById(level);
        String bet = (colored ? "§c" : "");

        return bet + LocaleI18n.get(nmsE.getName()) + " " + bet + LocaleI18n.get("enchantment.level." + level) + "§f";

    }

    public StackData[] getEnchantables(Player player) {
        List<StackData> items = new ArrayList<>();

        for (int i = 0; i <= 39; i++) {
            ItemStack it = player.getInventory().getItem(i);

            if (it != null && getEnchantment().canEnchantItem(it)) {
                if (it.containsEnchantment(enchantment) && it.getEnchantmentLevel(enchantment) >= level) {
                    continue;
                }

                items.add(new StackData(it, i));
            }
        }

        return items.toArray(new StackData[]{});
    }


    public void handleBuy(Player player, StackData data) {
        ItemStack atSlot = player.getInventory().getItem(data.slot);

        if (atSlot.getType() != data.item.getType()) {
            player.sendMessage(CC.RED + "Error! Attempted to enchant different item. Please try again later. (" + atSlot.toString() + " != " + data.item.toString() + " -> " + data.slot + ")");
            return;
        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.getStatistic().get(StatisticType.COINS) < getPrice()) {
            player.sendMessage(Locale.COINS_ERROR_INSUFFICIENT_FUNDS.format());
            return;
        }

        atSlot.addUnsafeEnchantment(getEnchantment(), getLevel());
        player.sendMessage(Locale.SHOP_ENCHANTED.format(ItemUtil.getName(atSlot), data.slot));
        purchased(playerData);

 //       new MarketMenu().openMenu(player);
    }

    @AllArgsConstructor
    public static class StackData {
        public ItemStack item;
        public int slot;
    }
}