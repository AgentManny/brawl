package rip.thecraft.brawl.spawn.team.command.leader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;

public class TeamDemoteCommand {

    @Command(names = {"team demote", "t demote", "f demote", "faction demote", "fac demote"})
    public static void execute(Player sender, CacheProfile player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || sender.isOp()) {
            if (team.isManager(player.getUuid())) {

                team.sendMessage(ChatColor.DARK_AQUA + player.getUsername() + " has been demoted to a member!");
                team.removeManager(player.getUuid());

            } else {
                sender.sendMessage(ChatColor.RED + "That player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be the team leader to demote players.");
        }
    }

}