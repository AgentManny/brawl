package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamPromoteCommand {

    @Command(names = {"team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain", "t captain", "f captain", "faction captain", "fac captain"})
    public void teamPromote(Player sender, SimpleOfflinePlayer target) {
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

                team.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been promoted to Manager!");
                team.addManager(target.getUuid());

            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "You must be the team leader to promote players.");
        }
    }
}
