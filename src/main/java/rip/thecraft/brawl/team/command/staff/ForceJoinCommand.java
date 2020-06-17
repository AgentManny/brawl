package rip.thecraft.brawl.team.command.staff;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceJoinCommand {

    @Command(names = { "forcejoin" }, permission = "brawl.team.forcejoin")
    public void execute(Player sender, Team team, @Param(defaultValue = "self") Player target) {
        if (Brawl.getInstance().getTeamHandler().getPlayerTeam(target) != null) {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
            }
            return;
        }
        team.addMember(target.getUniqueId());
        Brawl.getInstance().getTeamHandler().setTeam(target.getUniqueId(), team);
        target.sendMessage(ChatColor.GREEN + "You are now a member of §b" + team.getName() + "§a!");
        if (target != sender) {
            sender.sendMessage("§aPlayer added to team!");
        }
    }
}