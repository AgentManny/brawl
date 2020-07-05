package rip.thecraft.brawl.command.manage;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.command.Command;

import java.util.EnumSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SetSpawnCommand {

    private final Brawl plugin;

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(Player player) {
        this.setspawn(player, LocationType.SPAWN.name());
    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(Player player, String location) {

        LocationType type = LocationType.parse(location);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "Error: Location " + location + " doesn't exist.");
            player.sendMessage(ChatColor.GRAY + "Locations: " + ChatColor.WHITE + EnumSet.allOf(LocationType.class).stream().map(loc -> WordUtils.capitalize(loc.name().replace("_", " "))).collect(Collectors.joining(", ")));
            return;
        }

        Location loc = player.getLocation();

        plugin.setLocationByName(type.getName(), loc);
        player.sendMessage(ChatColor.GREEN + "Set location of " + ChatColor.WHITE + WordUtils.capitalize(type.name().toLowerCase().replace("_", " ")) + ChatColor.GREEN + " to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
    }

}
