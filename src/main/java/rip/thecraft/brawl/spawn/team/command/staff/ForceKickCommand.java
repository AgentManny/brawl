package rip.thecraft.brawl.spawn.team.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;

public class ForceKickCommand {

    @Command(names = "team admin forcekick", permission = "op")
    public static void forceKick(Player sender, CacheProfile player) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(player.getUuid());
        if (team == null) {
            sender.sendMessage(ChatColor.RED + player.getUsername() + " is not on a team!");
            return;
        }
        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + player.getUsername() + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }
        team.removeMember(player.getUuid());
        Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(player.getUuid());
        sender.sendMessage(ChatColor.GRAY + "Force-kicked " + player.getUsername() + " from their team, " + team.getName() + ".");
    }
}
