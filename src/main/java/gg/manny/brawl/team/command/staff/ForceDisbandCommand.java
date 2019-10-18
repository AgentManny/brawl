package gg.manny.brawl.team.command.staff;

import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {

    @Command(names = "forcedisband", permission = "op")
    public void execute(Player sender, Team team) {
        for (final Player online : team.getOnlineMembers()) {
            online.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force-disbanded the team.");
        }
        team.disband();
        sender.sendMessage(ChatColor.GRAY + "Force-disbanded the team " + team.getName() + ".");
    }

}
