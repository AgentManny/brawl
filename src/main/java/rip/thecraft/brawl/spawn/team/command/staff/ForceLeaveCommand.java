package rip.thecraft.brawl.spawn.team.command.staff;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaveCommand  {

    @Command(names = "forceleave", permission = "op")
    public static void forceLeave(Player player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getUniqueId());
        Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(player.getUniqueId());
        player.sendMessage(ChatColor.GRAY + "Force-left your team.");
    }
}
