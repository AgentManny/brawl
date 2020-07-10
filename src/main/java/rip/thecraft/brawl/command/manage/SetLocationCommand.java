package rip.thecraft.brawl.command.manage;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class SetLocationCommand {

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public static void setspawn(Player player) {
        setLocation(player, LocationType.SPAWN.name());
    }

    @Command(names = "setlocation", permission = "brawl.command.setspawn")
    public static void setLocation(Player player, @Param(name = "type [spawn, game lobby, arena...]") String location) {
        LocationType type = LocationType.parse(location);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "Error: Location " + location + " doesn't exist.");
            player.sendMessage(ChatColor.GRAY + "Locations: " + ChatColor.WHITE + EnumSet.allOf(LocationType.class).stream().map(loc -> WordUtils.capitalize(loc.name().replace("_", " "))).collect(Collectors.joining(", ")));
            return;
        }

        Location loc = player.getLocation();
        Brawl.getInstance().setLocationByName(type.getName(), loc);
        player.sendMessage(ChatColor.GREEN + "Set location of " + ChatColor.WHITE + WordUtils.capitalize(type.name().toLowerCase().replace("_", " ")) + ChatColor.GREEN + " to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        if (type.getUpdate() != null) {
            type.getUpdate().accept(player, loc);
        }
    }
}
