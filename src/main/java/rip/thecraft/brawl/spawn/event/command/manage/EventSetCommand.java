package rip.thecraft.brawl.spawn.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.property.codec.Codec;
import rip.thecraft.brawl.kit.ability.property.codec.Codecs;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.server.region.selection.Selection;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.MCommandHandler;
import rip.thecraft.spartan.command.Param;
import rip.thecraft.spartan.command.ParameterType;

import java.lang.reflect.Field;
import java.util.Map;

import static rip.thecraft.brawl.spawn.event.command.EventInfoCommand.getFriendlyName;

public class EventSetCommand {

    @Command(names = "event set", permission = "op", description = "Set a property for an event")
    public static void set(CommandSender sender, EventType eventType, String name, String property, @Param(defaultValue = "_") String newValue) {
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
                Codec<?> codec = Codecs.getCodecByClass(type);
                ParameterType<?> parameterType = MCommandHandler.getParameterType(type);
                if (!newValue.isEmpty() && !newValue.equals("_")) {
                    if (codec != null) {
                        try {
                            Object decode = codec.decode(newValue);
                            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.GREEN + newValue);
                            field.set(event, decode);
                            // TODO save
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                            return;
                        }
                    } else if (parameterType != null) {
                        Object transform = parameterType.transform(sender, newValue);
                        if (transform != null) {
                            try {
                                sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.GREEN + newValue);
                                field.set(event, transform);
                                // TODO save
                            } catch (Exception e) {
                                sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                                e.printStackTrace();
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + id + ChatColor.RED + " property is not editable.");
                    }
                } else { // They aren't setting a value by string, check if they are setting a value by player's data
                    if (!(sender instanceof Player)) return;

                    Player player = (Player) sender;
                    if (type.isAssignableFrom(Cuboid.class)) {
                        if (!player.getInventory().contains(Selection.SELECTION_WAND)) {
                            player.getInventory().addItem(Selection.SELECTION_WAND);
                        }
                        Selection selection = Selection.createOrGetSelection(player);
                        if (!selection.isFullObject()) {
                            player.sendMessage(ChatColor.RED + "You don't have a selection selected.");
                            return;
                        }

                        Cuboid cuboid = selection.createCuboid();
                        try {
                            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.GREEN + cuboid.getFriendlyName());
                            field.set(event, cuboid);
                            // TODO save
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                            e.printStackTrace();
                        }
                    } else if (type.isAssignableFrom(Location.class)) {
                        Location location = player.getLocation();
                        try {
                            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + event.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.GREEN + "Your location");
                            field.set(event, location);
                            // TODO save
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                            e.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Property " + property + " requires a value.");
                    }
                }
            }
        }
    }
}