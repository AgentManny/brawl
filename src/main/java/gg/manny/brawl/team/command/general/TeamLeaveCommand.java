package gg.manny.brawl.team.command.general;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamLeaveCommand {

    @Command(names = { "team leave", "t leave", "f leave", "faction leave", "fac leave" })
    public void execute(Player sender) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
            Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(sender.getUniqueId());
        } else {
            Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(sender.getUniqueId());
            team.flagForSave();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (team.isMember(player)) {
                    player.sendMessage(ChatColor.GRAY + sender.getName() + " has left the team.");
                }
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }
        Pivot.getInstance().getNametagHandler().reloadPlayer(sender);
        Pivot.getInstance().getNametagHandler().reloadOthersFor(sender);
    }
}
