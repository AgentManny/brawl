package rip.thecraft.brawl.warp.command;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class WarpHelpCommand {

    @Command(names = { "warp help", "warps help", "go help" }, permission = "op")
    public static void execute(Player sender) {
        String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warps");
        sender.sendMessage(CC.YELLOW + "/warps " + CC.GRAY + "List all available warps");
        sender.sendMessage(CC.YELLOW + "/warp goto" + CC.GRAY + "List all available warps");
        sender.sendMessage(line);
    }

}
