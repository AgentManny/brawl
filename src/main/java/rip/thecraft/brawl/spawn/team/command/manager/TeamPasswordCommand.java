package rip.thecraft.brawl.spawn.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;

public class TeamPasswordCommand {

    @Command(names = {"team password", "t password", "f password", "faction password", "fac password", "team pass", "t pass", "f pass", "faction pass", "fac pass", "team p", "t p", "f p", "faction p", "fac p"})
    public static void setPassword(Player sender, String password) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not in a team!");
            return;
        }

        if (password.equalsIgnoreCase("clear") || password.equalsIgnoreCase("remove") || password.equalsIgnoreCase("reset")) {
            team.setAnnouncement(null);
            team.flagForSave();
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has removed the team's password.");
            return;
        }

        team.setPassword(password);
        team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's password. ");
    }
}
