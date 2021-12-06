package rip.thecraft.brawl.market.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;

public class StatsResetButton extends MarketItem {

    public StatsResetButton() {
        super("Statistics Reset", Material.TNT, 2500);
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
    public boolean getRequiresSpawn() {
        return false;
    }

    @Override
    public void purchase(Player player, PlayerData playerData) {
        PlayerStatistic stats = playerData.getStatistic();
        for (StatisticType statistic : StatisticType.values()) {
            stats.set(statistic, statistic.getDefaultValue());
        }

        stats.getKitStatistics().forEach((name, kit) -> kit.reset());
        stats.getGameStatistics().forEach((game, stat) -> {
            stat.setPlayed(0);
            stat.setLosses(0);
            stat.setWins(0);
            stat.getProperties().clear();
        });
        playerData.getLevel().setCurrentExp(0);
        playerData.save();
        player.sendMessage(ChatColor.GREEN + "Your statistics have been wiped.");
    }
}