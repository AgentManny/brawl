package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.Command;

public class SpectateCommand {

    @Command(names = { "spec", "follow", "spectate" })
    public static void spectate(Player sender) {
        sender.sendMessage(ChatColor.RED + "Spectating is currently disabled.");
    }

}
