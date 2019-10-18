package gg.manny.brawl.team.command.manager;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamInviteCommand {

    @Command(names = { "team invite", "t invite", "f invite", "faction invite", "fac invite" })
    public void execute(Player sender, SimpleOfflinePlayer target) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
            return;
        }
        if (team.isOwner(sender.getUniqueId()) || team.isManager(sender.getUniqueId())) {
            if (!team.isMember(target.getUuid())) {
                if (team.getInvitations().contains(target.getUuid())) {
                    sender.sendMessage(ChatColor.RED + "That player has already been invited.");
                    return;
                }

                team.getInvitations().add(target.getUuid());
                team.flagForSave();
                if (target.getPlayer() != null) {
                    final Player targetPlayer = target.getPlayer();
                    targetPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");
                    new FancyMessage(ChatColor.DARK_AQUA + "Type '" + ChatColor.YELLOW + "/team join " + team.getName() + ChatColor.DARK_AQUA + "' or ")
                            .then(ChatColor.AQUA + "click here")
                            .tooltip(ChatColor.GREEN + "Join " + team.getName())
                            .command("/team join " + team.getName())
                            .then(ChatColor.DARK_AQUA + " to join.").send(targetPlayer);
                }
                for (final Player player : Brawl.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been invited to the team!");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That player is already on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a team manager (or above) to invite players.");
        }
    }

}
