package rip.thecraft.brawl.game.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.menu.GameSelectorMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

public class HostCommand {
    
    @Command(names = { "host", "event", "game" }, description = "Host an event")
    public static void execute(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isSpawnProtection()) {
            new GameSelectorMenu().openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to host events.");
        }
    }

}