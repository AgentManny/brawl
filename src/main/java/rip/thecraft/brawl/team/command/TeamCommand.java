package rip.thecraft.brawl.team.command;

import rip.thecraft.spartan.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names = { "team", "t", "f", "faction", "fac", "team help", "t help", "f help", "faction help", " fac help" })
    public static void execute(Player sender) {
        sender.sendMessage(ChatColor.DARK_AQUA.toString() + "*** General Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/t accept <teamName> [password] " + ChatColor.GRAY + " - Accept a pending invitation");
        sender.sendMessage(ChatColor.GRAY + "/t create <teamName>" + ChatColor.GRAY + " - Create a new team");
        sender.sendMessage(ChatColor.GRAY + "/t leave" + ChatColor.GRAY + " - Leave your current team");
        sender.sendMessage(ChatColor.GRAY + "/t chat - Toggle team chat only mode on or off.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA.toString() + "*** Information Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/t info [playerName|teamName]" + ChatColor.GRAY + " - Display team information");
        sender.sendMessage(ChatColor.GRAY + "/t list" + ChatColor.GRAY + " - Show list of teams online (sorted by most online)");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA.toString() + "*** Manager Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/t invite <player>" + ChatColor.GRAY + " - Invite a player to your team");
        sender.sendMessage(ChatColor.GRAY + "/t uninvite <player>" + ChatColor.GRAY + " - Revoke an invitation");
        sender.sendMessage(ChatColor.GRAY + "/t invites" + ChatColor.GRAY + " - List all open invitations");
        sender.sendMessage(ChatColor.GRAY + "/t kick <player>" + ChatColor.GRAY + " - Kick a player from your team");
        sender.sendMessage(ChatColor.GRAY + "/t password [password]" + ChatColor.GRAY + " - Set your team's password");
        sender.sendMessage(ChatColor.GRAY + "/t announcement [message here]" + ChatColor.GRAY + " - Set your team's announcement");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA.toString() + "*** Leader Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/t manager <add|remove> <player>" + ChatColor.GRAY + " - Add or remove a manager");
        sender.sendMessage(ChatColor.GRAY + "/t transfer <playerName> - Transfer ownership to another player.");
        sender.sendMessage(ChatColor.GRAY + "/t rename <newName>" + ChatColor.GRAY + " - Rename your team");
        sender.sendMessage(ChatColor.GRAY + "/t disband" + ChatColor.GRAY + " - Disband your team");
    }

}
