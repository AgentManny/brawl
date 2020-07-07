package rip.thecraft.brawl.kit.command.adapter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitCommandAdapter implements ParameterType<Kit> {

    @Override
    public Kit transform(CommandSender sender, String source) {
        Kit kit = Brawl.getInstance().getKitHandler().getKit(source);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit " + source + " not found.");
        }
        return kit;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            if (StringUtils.startsWithIgnoreCase(kit.getName(), source)) {
                completions.add(kit.getName());
            }
        }
        return completions;
    }

}
