package rip.thecraft.brawl.spawn.event.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventTypeCommandAdapter implements ParameterType<EventType> {

    @Override
    public EventType transform(CommandSender sender, String source) {
        EventType event = EventType.getByName(source);
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event type " + source + " not found.");
            List<String> matching = new ArrayList<>();
            for (EventType value : EventType.values()) {
                if (value.name().contains(source) || value.getShortName().contains(source) || value.getDisplayName().contains(source)) {
                    matching.add(WordUtils.capitalizeFully(value.name().toLowerCase())
                            .replace("_", "").replace(" ", ""));
                }
            }

            if (!matching.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Matching events (" + matching.size() + "): " + ChatColor.YELLOW + StringUtils.join(matching.toArray(), ", "));
            }
        }
        return event;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (EventType value : EventType.values()) {
            if (StringUtils.startsWithIgnoreCase(value.name(), source)) {
                completions.add(value.getDisplayName().replace(" ", ""));
            }
        }
        return completions;
    }
}
