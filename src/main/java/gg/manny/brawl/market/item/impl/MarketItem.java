package gg.manny.brawl.market.item.impl;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.market.MarketMenu;
import gg.manny.brawl.market.item.MarketPurchasableItem;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.util.inventory.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class MarketItem implements MarketPurchasableItem {

    private ItemUtil.ItemData itemData;
    private int amount;
    private int price;

    public String getFriendlyName() {
        return ItemUtil.getName(itemData.toItemStack());
    }

    public void handleBuy(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        if (playerData.getStatistic().get(StatisticType.COINS) < getPrice()) {
            player.sendMessage(Locale.COINS_ERROR_INSUFFICIENT_FUNDS.format());
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Locale.ERROR_INVENTORY_FULL.format());
            return;
        }

        ItemStack add = itemData.toItemStack();
        add.setAmount(amount);

        player.getInventory().addItem(add);
        player.sendMessage(Locale.SHOP_PURCHASED.format(amount, getFriendlyName()));
        purchased(playerData);
        new MarketMenu().openMenu(player);
    }

}