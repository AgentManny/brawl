package rip.thecraft.brawl.warp.command;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.warp.Warp;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class WarpListCommand {

    @Command(names = { "warp", "warps", "go", "warp list", "warps list", "go list" })
    public static void execute(Player sender) {
        if (Brawl.getInstance().getWarpManager().getWarps().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There aren't any warps available.");
            return;
        }

        String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warps");
        for (Warp warp : Brawl.getInstance().getWarpManager().getWarps().values()) {
            sender.sendMessage(CC.YELLOW + " - " + warp.getName() + (warp.getKit() != null && !warp.getKit().isEmpty() ? CC.LIGHT_PURPLE + " [" + warp.getKit() + "]" : ""));
        }
        sender.sendMessage(line);
    }

}
