package gg.manny.brawl.market.items;

import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitButton extends MarketItem {

    private final Kit kit;

    public KitButton(Kit kit, double storeMultiplier) {
        super("Rent " + kit.getName(), kit.getIcon().getType(), (int) (kit.getPrice() * storeMultiplier));

        this.kit = kit;
        setConfirm(true);
    }

    @Override
    public int getWeight() {
        return 9;
    }

    @Override
    public String getDescription() {
        return "Rent this kit for 24 hours";
    }

    @Override
    public void purchase(Player player, PlayerData playerData) {
        PlayerStatistic stats = playerData.getStatistic();
        stats.getSpawnStatistics().forEach((stat, value) -> {
            if (stat != StatisticType.CREDITS) {
                stats.set(stat, 0);
            }
        });
        stats.getKitStatistics().forEach((name, kit) -> {
            kit.setDeaths(0);
            kit.setUses(0);
            kit.setKills(0);
            kit.getProperties().clear();
        });
        stats.getGameStatistics().forEach((game, stat) -> {
            stat.setPlayed(0);
            stat.setLosses(0);
            stat.setWins(0);
            stat.getProperties().clear();
        });
        player.sendMessage(ChatColor.GREEN + "Your statistics have been wiped.");
    }
}