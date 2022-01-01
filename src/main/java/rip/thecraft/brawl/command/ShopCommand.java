package rip.thecraft.brawl.command;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.market.MarketMenu;
import rip.thecraft.spartan.command.Command;

public class ShopCommand {

    @Command(names = { "shop" })
    public static void execute(Player sender) {
        new MarketMenu().openMenu(sender);
    }

}
