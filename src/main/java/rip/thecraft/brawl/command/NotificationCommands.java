package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

public class NotificationCommands {

    @Command(names = {"toggleks", "togglekillstreaks"}, description = "Toggle killstreaks")
    public static void toggleKillstreaks(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.setKillstreakMessages(!playerData.isKillstreakMessages());
        String message = playerData.isKillstreakMessages() ? ChatColor.GREEN + "You have enabled killstreak messages." : ChatColor.RED + "You have disabled killstreak messages.";
        player.sendMessage(message);
    }

    @Command(names = {"togglegame", "togglegame"}, description = "Toggle game messages")
    public static void toggleGame(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.setGameMessages(!playerData.isGameMessages());
        String message = playerData.isGameMessages() ? ChatColor.GREEN + "You have enabled game messages." : ChatColor.RED + "You have disabled game messages.";
        player.sendMessage(message);
    }
}
