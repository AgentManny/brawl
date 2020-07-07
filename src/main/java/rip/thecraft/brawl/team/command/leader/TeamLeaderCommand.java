package rip.thecraft.brawl.team.command.leader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;

import java.util.UUID;

public class TeamLeaderCommand {

    @Command(names = { "team transfer", "t transfer", "f transfer", "faction transfer", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" })
    public static void transferLeader(Player sender, CacheProfile leader) {
        UUID uuid = leader.getUuid();

        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId())) {
            if (team.isMember(uuid)) {
                team.sendMessage(ChatColor.DARK_AQUA + leader.getUsername() + " has been transferred ownership from " + sender.getName() + "!");
                team.setOwner(uuid);
                team.addManager(sender.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be the team leader to transfer leadership!");
        }
    }
}
