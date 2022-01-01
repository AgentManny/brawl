package rip.thecraft.brawl.spawn.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.UUID;

public class TeamUninviteCommand {

    @Command(names = { "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" })
    public static void teamUninvite(Player sender, String name) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId()) || team.isManager(sender.getUniqueId())) {
            if (name.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                UUID remove = null;
                for (final UUID possibleUuid : team.getInvitations()) {
                    String possibleName = MUUIDCache.name(possibleUuid);
                    if (possibleName != null && possibleName.equalsIgnoreCase(name)) {
                        remove = possibleUuid;
                        break;
                    }
                }
                if (remove != null) {
                    team.getInvitations().remove(remove);
                    team.flagForSave();
                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + MUUIDCache.name(remove) + "!");
                } else {
                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + name + "'!");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a team manager (or above) to uninvite players.");
        }
    }
}
