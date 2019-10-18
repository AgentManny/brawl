package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamLeaderCommand {

    @Command(names = { "team transfer", "t transfer", "f transfer", "faction transfer", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" })
    public void execute(Player sender, SimpleOfflinePlayer leader) {
        UUID uuid = leader.getUuid();

        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId())) {
            if (team.isMember(uuid)) {
                team.sendMessage(ChatColor.DARK_AQUA + leader.getName() + " has been transferred ownership from " + sender.getName() + "!");
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
