package rip.thecraft.brawl.team.command.leader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;

public class TeamPromoteCommand {

    @Command(names = {"team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain", "t captain", "f captain", "faction captain", "fac captain"})
    public void teamPromote(Player sender, CacheProfile target) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId()) || sender.isOp()) {
            if (team.isMember(target.getUuid())) {
                if (team.isManager(target.getUuid())) {
                    sender.sendMessage(ChatColor.RED + "That player has already been promoted.");
                    return;
                }

                team.sendMessage(ChatColor.DARK_AQUA + target.getUsername() + " has been promoted to Manager!");
                team.addManager(target.getUuid());

            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "You must be the team leader to promote players.");
        }
    }
}
