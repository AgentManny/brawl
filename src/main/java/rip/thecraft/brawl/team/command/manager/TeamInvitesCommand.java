package rip.thecraft.brawl.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.UUID;

public class TeamInvitesCommand {

    @Command(names = {"team invites", "t invites", "f invites", "faction invites", "fac invites"})
    public void execute(Player sender) {
        StringBuilder yourInvites = new StringBuilder();
        for (Team team : Brawl.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.GRAY).append(", ");
            }
        }
        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "Your Invites: " + CC.GRAY + yourInvites.toString());
        Team current = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();
            for (UUID invites : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(MUUIDCache.name(invites)).append(ChatColor.GRAY).append(", ");
            }
            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }
            sender.sendMessage(ChatColor.DARK_AQUA + "Team Invites: " + CC.GRAY + invitedToYourTeam.toString());
        }
    }

}
