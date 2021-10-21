package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

/**
 * Created by Flatfile on 10/21/2021.
 */
public class GetKitCommand {

    @Command(names = { "getkit", "whatkit" }, description = "Tells you what kit a player has", async = true)
    public static void execute(Player sender, @Param(defaultValue = "self")PlayerData player){
        Kit kit = player.getSelectedKit();

        if(kit != null){
            sender.sendMessage(ChatColor.YELLOW + player.getName() + "'s kit: " + ChatColor.DARK_PURPLE + kit.getName());
        }else{
            sender.sendMessage(ChatColor.YELLOW + player.getName() + "'s kit: " + ChatColor.DARK_PURPLE + "None");
        }
    }

}
