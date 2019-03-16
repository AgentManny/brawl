package gg.manny.brawl.market.item;

import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;

public interface MarketPurchasableItem {

    int getPrice();

    default void purchased(PlayerData playerData) {
        playerData.toPlayer().sendMessage(Locale.COINS_DEDUCTED.format(getPrice()));
        PlayerStatistic statistic = playerData.getStatistic();
        statistic.set(StatisticType.COINS, statistic.get(StatisticType.COINS) - getPrice());
    }

}
