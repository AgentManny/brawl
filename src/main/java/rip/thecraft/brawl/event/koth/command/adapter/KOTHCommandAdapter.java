package rip.thecraft.brawl.event.koth.command.adapter;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.koth.KOTH;
import rip.thecraft.spartan.command.ParameterType;

public class KOTHCommandAdapter implements ParameterType<KOTH> {

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
