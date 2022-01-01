package rip.thecraft.brawl.spawn.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.spartan.command.Command;

import java.util.List;

public class EventCreateCommand {

    @Command(names = "event create", permission = "op", description = "Create an event")
    public static void create(CommandSender sender, EventType type, String name) {
        if (type.getRegistry() == null) {
            sender.sendMessage(ChatColor.RED + "This event is under development.");
            return;
        }

        EventHandler eh = Brawl.getInstance().getEventHandler();
        if (eh.getEvent(type, name) != null) {
            sender.sendMessage(ChatColor.RED + "Event " + name + " already exists for " + type.getShortName() + ".");
            return;
        }

        try {
            Class<? extends Event> eventClazz = type.getRegistry();
            Event event = eventClazz.getConstructor(String.class).newInstance(name);
            eh.getEvents().put(type, event);
            eh.save();
            sender.sendMessage(ChatColor.YELLOW + "Created event " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " for " + type.getColor() + type.getDisplayName() + ChatColor.YELLOW + ".");
            List<String> setupRequirements = event.getSetupRequirements();
            if (setupRequirements != null) {
                sender.sendMessage(ChatColor.YELLOW + "Requirements" + ChatColor.YELLOW + " (" + ChatColor.LIGHT_PURPLE + setupRequirements.size() + ChatColor.YELLOW + "):");
                setupRequirements.forEach(requirement -> sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + requirement));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}