package gg.manny.brawl.market.items;

import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatsResetButton extends MarketItem{

    public StatsResetButton() {
        super("Stats Reset", Material.SIGN, 5000);

        setConfirm(true);
    }

    @Override
    public int getWeight() {
        return 8;
    }

    @Override
    public String getDescription() {
        return "Resets your statistics";
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