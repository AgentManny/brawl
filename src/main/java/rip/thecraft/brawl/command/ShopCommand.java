package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.market.MarketMenu;
import rip.thecraft.spartan.command.Command;

public class ShopCommand {

    @Command(names = { "shop" })
    public void execute(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Opened the in-game shop.");
        new MarketMenu().openMenu(sender);
    }

}
