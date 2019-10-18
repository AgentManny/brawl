package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.brawl.team.command.general.TeamCreateCommand;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRenameCommand {

    @Command(names = { "team rename", "t rename", "f rename", "faction rename", "fac rename" })
    public void execute(Player sender, String name) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId())) {


            if (name.length() > 16) {
                sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                return;
            }
            if (name.length() < 3) {
                sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                return;
            }
            if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(name).find()) {
                if (Brawl.getInstance().getTeamHandler().getTeam(name) == null) {
                    team.rename(name);
                    team.sendMessage(ChatColor.DARK_AQUA + "Team renamed to " + name);
                } else {
                    sender.sendMessage(ChatColor.RED + "A team with that name already exists!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be the team leader to rename the team.");
        }
    }
}
