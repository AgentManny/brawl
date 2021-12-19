package rip.thecraft.brawl.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventHandler;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.spartan.command.Command;

public class EventSetCommand {

    @Command(names = "event set", permission = "op", description = "Set a property for an event")
    public static void set(CommandSender sender, EventType type, String name, String key) {
        EventHandler eh = Brawl.getInstance().getEventHandler();
        Event event = eh.getEvent(type, name);
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event " + name + " doesn't exist for " + type.getShortName() + ".");
            return;
        }

    }
}