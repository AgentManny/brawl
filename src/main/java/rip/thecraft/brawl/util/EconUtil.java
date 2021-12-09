package rip.thecraft.brawl.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.visual.tasks.LeaderboardUpdateTask;

import java.util.*;

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

    public static void pay(Player player, Player target, int amount){
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);

        if(target == player){
            player.sendMessage(ChatColor.RED + "You cannot pay yourself.");
            return;
        }

        if(amount < 1){
            player.sendMessage(ChatColor.RED + "You cannot pay less than 1 credit.");
            return;
        }

        if(!canAfford(playerData, amount)){
            player.sendMessage(ChatColor.RED + "You do not have enough credits for that payment.");
            return;
        }

        withdraw(playerData, amount);
        deposit(targetData, amount);

        String output = amount > 1 ? ChatColor.YELLOW + " credits" : ChatColor.YELLOW + " credit";
        player.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.LIGHT_PURPLE + amount + output + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + ".");
        target.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " sent you " + ChatColor.LIGHT_PURPLE + amount + output + ChatColor.YELLOW + ".");
    }

    public static void getBalance(Player player, Player target){
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);
        double bal = data.getStatistic().get(StatisticType.CREDITS);

        player.sendMessage(player == target ? ChatColor.YELLOW + "Your balance: " + ChatColor.LIGHT_PURPLE + bal : ChatColor.YELLOW + target.getName() + "'s Balance: " + ChatColor.LIGHT_PURPLE + bal);
    }

    public static void getBalanceTop(Player player){
        List<String> lines = new ArrayList<>();
        Map<String, Double> values = Brawl.getInstance().getLeaderboard().getSpawnLeaderboards().get(StatisticType.CREDITS);

        int entries = 0;
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            String prefix = ChatColor.WHITE.toString();
            switch (++entries) {
                case 1: {
                    prefix = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD;
                    break;
                }
                case 2: {
                    prefix = ChatColor.LIGHT_PURPLE.toString();
                    break;
                }
                case 3: {
                    prefix = ChatColor.YELLOW.toString();
                    break;
                }
            }
            lines.add(prefix + entries + ". " + ChatColor.WHITE + entry.getKey() + ChatColor.GRAY + " \u2758 " + ChatColor.WHITE + LeaderboardUpdateTask.STAT_FORMAT.format(entry.getValue()));
        }

        lines.add(0, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));
        lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));

        for(String message : lines){
            player.sendMessage(message);
        }
    }

}
