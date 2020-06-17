package rip.thecraft.brawl.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.killstreak.Killstreak;
import rip.thecraft.brawl.killstreak.KillstreakHandler;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class KillstreakCommand {

    private final Brawl plugin;

    @Command(names = "killstreak", permission = "op")
    public void execute(Player player, int newStreak) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        playerData.getStatistic().set(StatisticType.KILLSTREAK, newStreak);
        KillstreakHandler handler = Brawl.getInstance().getKillstreakHandler();

        if (handler.getStreaks().containsKey(newStreak)) {
            Killstreak killstreak = handler.getStreaks().get(newStreak);
            killstreak.onKill(player, playerData);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " has gotten a killstreak of " + ChatColor.LIGHT_PURPLE + newStreak + ChatColor.YELLOW + " and received " + killstreak.getColor() + killstreak.getName() + ChatColor.YELLOW + ".");
            }
        }
    }
}
