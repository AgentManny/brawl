package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

/**
 * Created by Flatfile on 10/21/2021.
 */
public class GetKitCommand {

    @Command(names = { "getkit", "whatkit", "gk" }, description = "Tells you what kit a player has")
    public static void execute(Player sender, @Param(defaultValue = "self") Player player){
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Kit kit = playerData.getSelectedKit();

        sender.sendMessage(ChatColor.WHITE + player.getDisplayName() + (kit == null ? ChatColor.RED + " is not using a kit" : ChatColor.YELLOW + " is using " + ChatColor.LIGHT_PURPLE + kit.getName() + ChatColor.YELLOW) + ".");
    }

}
