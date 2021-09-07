package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.spartan.command.Command;

public class HelpCommand {

    @Command(names = { "help", "?", "guide" })
    public static void execute(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "*** General Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/kit" + ChatColor.GRAY + " - Opens the kit selector.");
        sender.sendMessage(ChatColor.GRAY + "/kit [name]" + ChatColor.GRAY + " - Choose a kit by their name.");
        sender.sendMessage(ChatColor.GRAY + "/settings " + ChatColor.GRAY + " - Modify your settings");
        sender.sendMessage(ChatColor.GRAY + "/stats [player] " + ChatColor.GRAY + " - View a player's statistics");
        sender.sendMessage(ChatColor.GRAY + "/refill [soup|potion] " + ChatColor.GRAY + " - Set your healing method");
        sender.sendMessage(ChatColor.GRAY + "/team " + ChatColor.GRAY + " - View team commands");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "*** Teleportation Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/spawn" + ChatColor.GRAY + " - Teleport to spawn.");
        sender.sendMessage(ChatColor.GRAY + "/1v1" + ChatColor.GRAY + " - Teleport to the duel arena.");
        sender.sendMessage(ChatColor.GRAY + "/warp [name]" + ChatColor.GRAY + " - Teleport to a warp");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "*** Shop Commands ***");
        sender.sendMessage(ChatColor.GRAY + "/shop" + ChatColor.GRAY + " - Opens the shop selector.");
        sender.sendMessage(ChatColor.GRAY + "/refill" + ChatColor.GRAY + " - Refills your soups " + ChatColor.WHITE + "[50 credits]");
        sender.sendMessage(ChatColor.GRAY + "/repair" + ChatColor.GRAY + " - Repair your armor and weapons " + ChatColor.WHITE + "[100 credits]");
        sender.sendMessage("");
    }

}
