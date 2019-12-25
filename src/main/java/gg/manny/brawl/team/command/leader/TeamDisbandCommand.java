package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDisbandCommand {

    @Command(names = { "team disband", "t disband", "f disband", "faction disband", "fac disband" })
    public void execute(Player player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the team leader to disband!");
            return;
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has disbanded the team.");
        team.disband();
    }
}