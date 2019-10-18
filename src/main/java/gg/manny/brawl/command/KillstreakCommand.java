package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.killstreak.KillstreakHandler;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.util.chatcolor.CC;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
            for (Player online : Bukkit.getOnlinePlayers()) {
                killstreak.onKill(player, playerData);
                online.sendMessage(gg.manny.pivot.util.chatcolor.CC.WHITE + player.getDisplayName() + gg.manny.pivot.util.chatcolor.CC.YELLOW + " has gotten a killstreak of " + ChatColor.LIGHT_PURPLE + newStreak + CC.YELLOW + " and received " + killstreak.getColor() + killstreak.getName() + gg.manny.pivot.util.chatcolor.CC.YELLOW + ".");
            }
        }
    }
}
