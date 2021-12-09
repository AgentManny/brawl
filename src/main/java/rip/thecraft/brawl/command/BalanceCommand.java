package rip.thecraft.brawl.command;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

/**
 * Created by Flatfile on 12/9/2021.
 */
public class BalanceCommand {

    @Command(names = { "balance", "bal" }, description = "Check a players balance")
    public static void execute(Player sender, @Param(defaultValue = "self") Player target){
        EconUtil.getBalance(sender, target);
    }

    @Command(names = { "balancetop", "baltop" }, description = "Check the top balances on the server.")
    public static void execute(Player player){
        EconUtil.getBalanceTop(player);
    }

}
