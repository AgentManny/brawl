package rip.thecraft.brawl.spawn.warp;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
public class WarpTypeAdapter implements ParameterType<Warp> {

    @Override
    public Warp transform(CommandSender sender, String source) {
        WarpManager wm = Brawl.getInstance().getWarpManager();
        if (source.equalsIgnoreCase("list")) {
            if (wm.getWarps().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "There aren't any warps available.");
                return null;
            }

            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "*** Warps ***");
            for (Warp warp : Brawl.getInstance().getWarpManager().getWarps().values()) {
                sender.sendMessage(CC.GRAY + " - " + ChatColor.WHITE + warp.getName() + (warp.getKit() != null && !warp.getKit().isEmpty() ? CC.LIGHT_PURPLE + " [" + warp.getKit() + "]" : ""));
            }
            sender.sendMessage(ChatColor.GRAY + "Type /warp <warp> to teleport to a warp");
            sender.sendMessage(" ");
            return null;
        }

        Warp warp = wm.getWarp(source);
        if (warp == null) {
            sender.sendMessage(CC.RED + "Warp " + source + " not found.");
        }
        return warp;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Warp warp : Brawl.getInstance().getWarpManager().getWarps().values()) {
            if (StringUtils.startsWithIgnoreCase(warp.getName(), source)) {
                completions.add(warp.getName());
            }
        }
        return completions;
    }
}
