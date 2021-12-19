package rip.thecraft.brawl.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventHandler;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.spartan.command.Command;

public class EventRemoveCommand {

    @Command(names = "event remove", permission = "op", description = "Removes an event")
    public static void create(CommandSender sender, EventType type, String name) {
        EventHandler eh = Brawl.getInstance().getEventHandler();
        Event event = eh.getEvent(type, name);
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event " + name + " doesn't exist for " + type.getShortName() + ".");
            return;
        }

        eh.getEvents().get(type).remove(event);
        sender.sendMessage(ChatColor.YELLOW + "Removed event " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " from " + type.getShortName() + ".");
    }
}