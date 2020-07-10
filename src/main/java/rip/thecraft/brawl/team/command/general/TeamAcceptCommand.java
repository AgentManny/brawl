package rip.thecraft.brawl.team.command.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;
import rip.thecraft.spartan.nametag.NametagHandler;

public class TeamAcceptCommand {

    @Command(names = {"team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join"})
    public static void accept(Player sender, Team team, @Param(defaultValue = "_") String password) {
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
            NametagHandler.reloadPlayer(sender);
            NametagHandler.reloadOthersFor(sender);
        } else {
            sender.sendMessage(ChatColor.RED + (password.equals("_") ? "This team has not invited you!" : "Password does not match."));
        }
    }
}
