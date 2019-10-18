package gg.manny.brawl.warp;

import com.google.common.base.Strings;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WarpTypeAdapter implements CommandTypeAdapter<Warp> {

    private final WarpManager wm;

    @Override
    public Warp transform(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("list")) {
            if (wm.getWarps().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "There aren't any warps available.");
                return null;
            }

            String line = CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 35);
            sender.sendMessage(line);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warps");
            for (Warp warp : wm.getWarps().values()) {
                sender.sendMessage(CC.YELLOW + " - " + warp.getName() + (warp.getKit() != null && !warp.getKit().isEmpty() ? CC.LIGHT_PURPLE + " [" + warp.getKit() + "]" : ""));
            }
            sender.sendMessage(line);
            return null;
        }

        Warp warp = wm.getWarp(source);
        if (warp == null) {
            sender.sendMessage(CC.RED + "Warp " + source + " not found.");
        }
        return warp;
    }

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Warp warp : wm.getWarps().values()) {
            if (StringUtils.startsWithIgnoreCase(warp.getName(), source)) {
                completions.add(warp.getName());
            }
        }
        return completions;
    }
}
