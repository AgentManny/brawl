package rip.thecraft.brawl.spawn.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.spartan.command.Command;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class EventAddCommand {

    @Command(names = "event add", permission = "op", description = "Set a property for an event")
    public static void add(Player sender, EventType eventType, String name, String property) {
        EventHandler eh = Brawl.getInstance().getEventHandler();
        Event event = eh.getEvent(eventType, name);
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event " + name + " doesn't exist for " + eventType.getShortName() + ".");
            return;
        }

        Map<String, Field> properties = event.getProperties();
        for (Map.Entry<String, Field> entry : properties.entrySet()) {
            String id = entry.getKey();
            Field field = entry.getValue();
            Class<?> type = field.getType();
            if (id.replace("-", "").equalsIgnoreCase(property.replace("-", ""))) {
                if (!type.isAssignableFrom(List.class)) {
                    sender.sendMessage(ChatColor.RED + "Property " + ChatColor.YELLOW + property + ChatColor.RED + " is not a list");
                    return;
                }

                try {
                    List<Location> locations = (List<Location>) field.get(event);
                    locations.add(sender.getLocation());
                    sender.sendMessage(ChatColor.YELLOW + "Added location to " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + ": " + ChatColor.GREEN + sender.getLocation().serialize()
                            .values().stream().map(Object::toString).reduce("", String::concat));
                    field.set(event, locations);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}