package rip.thecraft.brawl.spawn.team.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;

public class ForceDisbandCommand {

    @Command(names = "team admin forcedisband", permission = "op")
    public static void forceDisband(Player sender, Team team) {
        for (final Player online : team.getOnlineMembers()) {
            online.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force-disbanded the team.");
        }
        team.disband();
        sender.sendMessage(ChatColor.GRAY + "Force-disbanded the team " + team.getName() + ".");
    }

}
