package gg.manny.brawl.team.command.general;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAcceptCommand {

    @Command(names = {"team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join"})
    public void execute(Player sender, Team team, @Parameter(value = "_") String password) {
        if (Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "You are already on a team!");
            return;
        }

        if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
            sender.sendMessage(ChatColor.RED + "This team is full!");
            return;
        }

        if (team.getInvitations().contains(sender.getUniqueId()) || (team.getPassword() != null && team.getPassword().equals(password))) {
            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            team.sendMessage(ChatColor.AQUA + sender.getName() + " joined the team.");
            Brawl.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);
            Pivot.getInstance().getNametagHandler().reloadPlayer(sender);
            Pivot.getInstance().getNametagHandler().reloadOthersFor(sender);
        } else {
            sender.sendMessage(ChatColor.RED + (password.equals("_") ? "This team has not invited you!" : "Password does not match."));
        }
    }
}
