package gg.manny.brawl.team.command.leader;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamManagerCommand {


    @Command(names = {"team manager", "t manager", "f manager", "faction manager", "fac manager"})
    public void execute(Player sender) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /team manager <add|remove> <player>");
    }

    @Command(names = {"team manager add", "t manager add", "f manager add", "faction manager add", "fac manager add"})
    public void add(Player sender, SimpleOfflinePlayer target) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isMember(target.getUuid())) {
            if (team.isOwner(sender.getUniqueId())) {
                if (team.isManager(target.getUuid())) {
                    sender.sendMessage(ChatColor.RED + "That player is already a Manager!");
                } else {
                    team.addManager(sender.getUniqueId());
                    team.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been promoted to Manager!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must be the owner of the team to add managers.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not on your team.");
        }
    }

    @Command(names = {"team manager remove", "t manager remove", "f manager remove", "faction manager remove", "fac manager remove"})
    public void remove(Player sender, SimpleOfflinePlayer target) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isMember(target.getUuid())) {
            if (team.isOwner(sender.getUniqueId())) {
                if (!team.isManager(target.getUuid())) {
                    sender.sendMessage(ChatColor.RED + "That player is not a manager!");
                } else {
                    team.removeManager(sender.getUniqueId());
                    team.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been demoted from Manager!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must be the owner of the team to remove managers.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not on your team.");
        }
    }

}
