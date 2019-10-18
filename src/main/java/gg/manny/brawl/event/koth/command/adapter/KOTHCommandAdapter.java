package gg.manny.brawl.event.koth.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.event.koth.KOTH;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHCommandAdapter implements CommandTypeAdapter<KOTH> {

    @Override
    public KOTH transform(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("active")) {
            KOTH koth = Brawl.getInstance().getEventHandler().getActiveKOTH();
            if (koth == null) {
                sender.sendMessage(ChatColor.RED + "There isn't a KOTH active right now.");
            }
            return koth;
        }

        KOTH koth = Brawl.getInstance().getEventHandler().getKOTHByName(source);
        if (koth == null) {
            sender.sendMessage(ChatColor.RED + "KOTH " + source + " not found.");
        }
        return koth;
    }
}
