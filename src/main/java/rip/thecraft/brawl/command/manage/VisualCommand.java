package rip.thecraft.brawl.command.manage;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.visual.VisualManager;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class VisualCommand {

    private final Brawl plugin;

    @Command(names = "visual setloc stats", permission = "op")
    public void setPlayerStats(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Set player statistics hologram to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        plugin.setLocationByName(VisualManager.HOLO_STATS, player.getLocation());
    }

    @Command(names = "visual setloc lb", permission = "op")
    public void setLeaderboardStats(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Set leaderboard hologram to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        plugin.setLocationByName(VisualManager.HOLO_LB, player.getLocation());
    }

}
