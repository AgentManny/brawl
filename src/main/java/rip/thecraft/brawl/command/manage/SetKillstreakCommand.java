package rip.thecraft.brawl.command.manage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.killstreak.Killstreak;
import rip.thecraft.brawl.spawn.killstreak.KillstreakHandler;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;

public class SetKillstreakCommand {

    @Command(names = "setkillstreak", permission = "op")
    public static void execute(Player player, int newStreak) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.getStatistic().set(StatisticType.KILLSTREAK, newStreak);
        KillstreakHandler handler = Brawl.getInstance().getKillstreakHandler();

        if (handler.getStreaks().containsKey(newStreak)) {
            Killstreak killstreak = handler.getStreaks().get(newStreak);
            killstreak.onKill(player, playerData);
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerData onlineData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(online);
                if (onlineData.isKillstreakMessages()) {
                    online.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " has gotten a killstreak of " + ChatColor.LIGHT_PURPLE + newStreak + ChatColor.YELLOW + " and received " + killstreak.getColor() + killstreak.getName() + ChatColor.YELLOW + ".");
                }
            }
        }
    }
}
