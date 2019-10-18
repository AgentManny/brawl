package gg.manny.brawl.warp.command;

import com.google.common.base.Strings;
import gg.manny.brawl.warp.Warp;
import gg.manny.brawl.warp.WarpManager;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WarpListCommand {

    private final WarpManager wm;

    @Command(names = { "warp", "warps", "go", "warp list", "warps list", "go list" })
    public void execute(Player sender) {
        if (wm.getWarps().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There aren't any warps available.");
            return;
        }

        String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warps");
        for (Warp warp : wm.getWarps().values()) {
            sender.sendMessage(CC.YELLOW + " - " + warp.getName() + (warp.getKit() != null && !warp.getKit().isEmpty() ? CC.LIGHT_PURPLE + " [" + warp.getKit() + "]" : ""));
        }
        sender.sendMessage(line);
    }

}
