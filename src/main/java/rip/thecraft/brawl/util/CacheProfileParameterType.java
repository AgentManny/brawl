package rip.thecraft.brawl.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.command.ParameterType;
import rip.thecraft.spartan.visibility.MVisibilityHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Only used to get players that don't exist ect -- Could be used for punishments and setting ranks
public class CacheProfileParameterType implements ParameterType<CacheProfile> {

    @Override
    public CacheProfile transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equalsIgnoreCase("self")) {
            return new CacheProfile(((Player) sender).getUniqueId(), sender.getName());
        }

        try {
            return new CacheProfile(source);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Player " + source + " not found.");
            return null;
        }
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Player player : Brawl.getInstance().getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && MVisibilityHandler.treatAsOnline(player, sender)) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
