package gg.manny.brawl.duelarena.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.arena.Arena;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArenaCommandAdapter implements CommandTypeAdapter<Arena> {

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
}
