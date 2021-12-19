package rip.thecraft.brawl.event.command;

import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventHandler;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.spartan.command.Command;

import java.lang.reflect.Field;
import java.util.Map;

public class EventInfoCommand {

    @Command(names = "event info", permission = "op", description = "Information of an event")
    public static void eventInfo(CommandSender sender, String name) {
        EventHandler eventHandler = Brawl.getInstance().getEventHandler();
        boolean found = false;
        for (EventType type : EventType.values()) {
            for (Event event : eventHandler.getEvents().get(type)) {
                if (event.getName().equalsIgnoreCase(name)) {
                    display(sender, event);
                    found = true;
                }
            }
        }
        if (!found) {
            sender.sendMessage(ChatColor.RED + "Event " + name + " not found.");
        }
    }

    private static void display(CommandSender sender, Event event) {
        sender.sendMessage(ChatColor.YELLOW + "Event " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + " (" + ChatColor.LIGHT_PURPLE + event.getType().getShortName() + ChatColor.YELLOW + "): " + (event.isSetup() ? ChatColor.GREEN + "Setup" : ChatColor.RED + "Not Setup"));
        Map<String, Field> properties = event.getProperties();
        sender.sendMessage(ChatColor.YELLOW + "> Properties (" + ChatColor.LIGHT_PURPLE + properties.size() + ChatColor.YELLOW + "):");
        for (Map.Entry<String, Field> entry : properties.entrySet()) {
            String key = entry.getKey();
            Field value = entry.getValue();
            try {
                AbilityProperty property = value.getAnnotation(AbilityProperty.class);
                Object val = value.get(event);
                String friendlyValue = val != null ? value.getType().isAssignableFrom(Cuboid.class) ? ((Cuboid)val).getFriendlyName() :
                                value.getType().isAssignableFrom(Location.class) ? "(" + ((Location)val).serialize()
                                        .values().stream().map(Object::toString).reduce("", String::concat)
                                        + ")" :
                                        String.valueOf(val) : "Not set";
                new FancyMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + getFriendlyName(key) + ChatColor.GRAY + " (" + value.getType().getSimpleName() + ")" + ChatColor.YELLOW + ": " + (val== null ? ChatColor.RED + "Not set" : ChatColor.LIGHT_PURPLE + friendlyValue))
                        .tooltip(
                                ChatColor.GRAY + "Property " + ChatColor.WHITE + key,
                                ChatColor.GRAY + "Description: " + (property.description().isEmpty() ? ChatColor.RED + "None" : ChatColor.WHITE + property.description())
                        ).suggest("/event set " + event.getType().getShortName().toLowerCase() + " " + event.getName().toLowerCase().replace(" ", "") + " " + key)
                        .send(sender);
            } catch (IllegalAccessException ignored) {
            }
        }
        sender.sendMessage(ChatColor.GRAY + "Hover a property for more information");
        sender.sendMessage(ChatColor.RED + "Usage: /event set " + event.getType().getShortName().toLowerCase() + " " + event.getName().toLowerCase().replace(" ", "") + " <property> <newValue>");
    }

    public static String getFriendlyName(String id) {
        return WordUtils.capitalizeFully(id.replace("/([A-Z])/g", " $1").trim()
                .replace("_", " ")
                .replace("-", " "));
    }
}
