package rip.thecraft.brawl.spawn.team.command.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;

import java.util.regex.Pattern;

public class TeamCreateCommand {

    public final static Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");

    @Command(names = { "team create", "t create", "f create", "faction create", "fac create" })
    public static void create(Player sender, String name) {
        if (Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId()) == null) {
            if (ALPHA_NUMERIC.matcher(name).find()) {
                sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
                return;
            }
            if (name.length() > 16) {
                sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                return;
            }
            if (name.length() < 3) {
                sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                return;
            }
            if (Brawl.getInstance().getTeamHandler().getTeam(name) == null) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
                sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");
                final Team team = new Team(name, sender.getUniqueId());
                Brawl.getInstance().getTeamHandler().addTeam(team);
                Brawl.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);
                team.flagForSave();
            } else {
                sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
        }
    }

}
