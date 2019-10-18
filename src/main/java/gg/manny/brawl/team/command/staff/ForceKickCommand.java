package gg.manny.brawl.team.command.staff;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceKickCommand {

    @Command(names = "forcekick", permission = "op")
    public void execute(Player sender, SimpleOfflinePlayer player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(player.getUuid());
        if (team == null) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on a team!");
            return;
        }
        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + player.getName() + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }
        team.removeMember(player.getUuid());
        Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(player.getUuid());
        sender.sendMessage(ChatColor.GRAY + "Force-kicked " + player.getName() + " from their team, " + team.getName() + ".");
    }
}
