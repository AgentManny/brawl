package rip.thecraft.brawl.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.arena.Arena;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArenaCommandAdapter implements ParameterType<Arena> {

    @Override
    public Arena transform(CommandSender sender, String source) {
        Arena arena = Brawl.getInstance().getMatchHandler().getArenas().stream()
                .filter(value -> value.getName().equalsIgnoreCase(source))
                .findAny()
                .orElse(null);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena " + source + " not found.");
        }
        return arena;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Arena arena : Brawl.getInstance().getMatchHandler().getArenas()) {
            if (StringUtils.startsWithIgnoreCase(arena.getName(), source)) {
                completions.add(arena.getName());
            }
        }
        return completions;
    }
}
