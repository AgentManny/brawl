package rip.thecraft.brawl.util;

import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;

/**
 * Created by Flatfile on 10/21/2021.
 */
public class EconUtil {

    public static boolean canAfford(PlayerData data, double price){
        return data.getStatistic().get(StatisticType.CREDITS) >= price;
    }

    public static void deposit(PlayerData data, double amount){
        data.getStatistic().add(StatisticType.CREDITS, amount);
    }

    public static void withdraw(PlayerData data, double amount){
        double credits = data.getStatistic().get(StatisticType.CREDITS);
        data.getStatistic().set(StatisticType.CREDITS, credits - amount);
    }

}
