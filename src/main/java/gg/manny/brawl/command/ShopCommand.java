package gg.manny.brawl.command;

import gg.manny.brawl.market.MarketMenu;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ShopCommand {

    @Command(names = { "shop" })
    public void execute(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Opened the in-game shop.");
        new MarketMenu().openMenu(sender);
    }

}
