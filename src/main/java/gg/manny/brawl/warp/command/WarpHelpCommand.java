package gg.manny.brawl.warp.command;

import com.google.common.base.Strings;
import gg.manny.brawl.warp.WarpManager;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WarpHelpCommand {

    private final WarpManager wm;

    @Command(names = { "warp help", "warps help", "go help" }, permission = "op")
    public void execute(Player sender) {
        String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warps");
        sender.sendMessage(CC.YELLOW + "/warps " + CC.GRAY + "List all available warps");
        sender.sendMessage(CC.YELLOW + "/warp goto" + CC.GRAY + "List all available warps");
        sender.sendMessage(line);
    }

}
