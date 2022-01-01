package rip.thecraft.brawl.spawn.team.adapter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamTypeAdapter implements ParameterType<Team> {

    @Override
    public Team transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(((Player) sender));
            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
            }
            return team;
        }

        Team team = Brawl.getInstance().getTeamHandler().getTeam(source);
        if (team == null) {
            Player bukkitPlayer = Brawl.getInstance().getServer().getPlayer(source);
            if (bukkitPlayer != null) {
                team = Brawl.getInstance().getTeamHandler().getPlayerTeam(bukkitPlayer);
            }
            if (team == null) {
                sender.sendMessage(ChatColor.RED + "No team with the name or member " + source + " found.");
                return null;
            }
        }
        return team;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Team team : Brawl.getInstance().getTeamHandler().getTeams()) {
            if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
                completions.add(team.getName());
            }
        }
        for (Player player : Brawl.getInstance().getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                completions.add(player.getName());
            }
        }
        return completions;
    }

}
