package rip.thecraft.brawl.spawn.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;

public class TeamAnnouncementCommand {

    @Command(names = {"team announcement", "t announcement", "f announcement", "faction announcement", "fac announcement", "team desc", "t desc", "f desc", "faction desc", "fac desc", "team description", "t description", "f description", "faction description", "fac description"})
    public static void setAnnouncement(Player sender, String announcement) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not in a team!");
            return;
        }

        if (announcement.equalsIgnoreCase("clear") || announcement.equalsIgnoreCase("remove") || announcement.equalsIgnoreCase("reset")) {
            team.setAnnouncement(null);
            team.flagForSave();
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has cleared the team's announcement.");
            return;
        }

        team.setAnnouncement(announcement);
        team.flagForSave();
        team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's announcement:");
        team.sendMessage(ChatColor.GRAY + announcement);
    }
}
