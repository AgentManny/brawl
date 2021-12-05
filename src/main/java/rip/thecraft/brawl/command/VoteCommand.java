package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.Command;

public class VoteCommand {

    @Command(names = "vote", description = "Vote for the server")
    public static void execute(Player sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Vote for us on " + ChatColor.BOLD + "NameMC" + ChatColor.LIGHT_PURPLE + " for a free " + ChatColor.BOLD + "Kit Pass" + ChatColor.LIGHT_PURPLE + " every 12 hours.");
        sender.sendMessage(ChatColor.GRAY + "Vote for us on " + ChatColor.WHITE + "https://namemc.com/server/kaze.gg" + ChatColor.GRAY + " for rewards.");
        sender.sendMessage( " ");
        sender.sendMessage(ChatColor.GRAY + "If you have already voted, relog for your rewards.");
    }
}
