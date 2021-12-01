package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.Command;

public class ShopCommand {

    @Command(names = { "shop" })
    public static void execute(Player sender) {
        sender.sendMessage(ChatColor.RED + "There is currently nothing available in the shop. Please check back later.");
    }

}
