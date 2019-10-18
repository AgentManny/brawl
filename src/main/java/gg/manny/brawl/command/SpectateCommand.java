package gg.manny.brawl.command;

import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectateCommand {

    @Command(names = { "spec", "follow", "spectate" })
    public void spectate(Player sender) {
        sender.sendMessage(ChatColor.RED + "Spectating is currently disabled.");
    }

}
