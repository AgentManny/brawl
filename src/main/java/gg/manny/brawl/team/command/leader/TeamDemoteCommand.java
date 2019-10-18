package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDemoteCommand {

    @Command(names = {"team demote", "t demote", "f demote", "faction demote", "fac demote"})
    public void execute(Player sender, SimpleOfflinePlayer player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || sender.isOp()) {
            if (team.isManager(player.getUuid())) {

                team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been demoted to a member!");
                team.removeManager(player.getUuid());

            } else {
                sender.sendMessage(ChatColor.RED + "That player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be the team leader to demote players.");
        }
    }

}