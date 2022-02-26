package rip.thecraft.brawl.spawn.launchpad;

import gg.manny.streamline.command.annotation.Command;
import gg.manny.streamline.command.annotation.Require;
import gg.manny.streamline.command.annotation.Sender;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;

import java.util.List;

import static rip.thecraft.brawl.spawn.launchpad.LaunchpadHandler.JUMP_METADATA;

@Require("brawl.launchpad")
public class LaunchpadCommand {

    @Command(name = "jump", desc = "Jump to a location manually")
    public void jump(@Sender Player player, double x, double y, double z) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.getSpawnData().throwPlayer(new Location(player.getWorld(), x, y, z));
        player.sendMessage(ChatColor.GREEN + "Launching: " + ChatColor.WHITE + x + ", " + y + ", " + z);
    }

    @Command(name = "test", desc = "Jump to a optimal location")
    public void test(@Sender Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        LaunchpadHandler launchpadHandler = Brawl.getInstance().getLaunchpadHandler();
        playerData.getSpawnData().throwPlayer(launchpadHandler.getOptimalLocation(launchpadHandler.getRandomLocation()));
        player.sendMessage(ChatColor.GREEN + "Launching to a suitable location");
    }

    @Command(name = "toggle", desc = "View and modify launchpads locations around the map")
    public void toggle(@Sender Player player) {
        LaunchpadHandler launchpadHandler = Brawl.getInstance().getLaunchpadHandler();
        List<Location> locations = launchpadHandler.getLocations();
        if (player.hasMetadata(JUMP_METADATA)) {
            player.sendMessage(ChatColor.RED + "You are no longer viewing Launchpad locations.");
            locations.forEach(location -> player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData()));
            player.removeMetadata(JUMP_METADATA, Brawl.getInstance());
        } else {
            player.sendMessage(ChatColor.GREEN + "You are now viewing Launchpad locations. Placing/Removing an Emerald will remove/add locations.");
            locations.forEach(location -> player.sendBlockChange(location, Material.EMERALD_BLOCK, (byte) 0));
            player.setMetadata(JUMP_METADATA, new FixedMetadataValue(Brawl.getInstance(), true));
        }
    }

    @Command(name = "list", desc = "List all launchpads across thhe map")
    public void list(@Sender Player player) {
        LaunchpadHandler launchpadHandler = Brawl.getInstance().getLaunchpadHandler();
        List<Location> locations = launchpadHandler.getLocations();
        player.sendMessage(ChatColor.YELLOW + "Launchpad Locations (" + ChatColor.LIGHT_PURPLE + locations.size() + ChatColor.YELLOW + "):");
        locations.forEach(location -> {
            String friendlyLoc = location.toBlockLocation().toVector().toString().replace(",", ", ");
            new FancyMessage(" - ")
                    .color(ChatColor.YELLOW)
                    .then(friendlyLoc)
                    .color(ChatColor.LIGHT_PURPLE)
                    .suggest("/tppos " + friendlyLoc.replace(",", ""))
                    .tooltip(ChatColor.LIGHT_PURPLE + "Teleport to " + ChatColor.YELLOW + friendlyLoc)
                    .send(player);
        });
    }

}
