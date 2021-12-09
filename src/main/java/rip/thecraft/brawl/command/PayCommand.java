package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.spartan.command.Command;

/**
 * Created by Flatfile on 12/9/2021.
 */
public class PayCommand {

    @Command(names = { "pay", "p2p", "sendmoney" }, permission = "brawl.pay", description = "Pay a player")
    public static void execute(Player sender, Player target, int amount){
        try{
            EconUtil.pay(sender, target, amount);
        }catch (NumberFormatException ex){
            sender.sendMessage(ChatColor.RED + "Please input a proper number.");
        }
    }

}
