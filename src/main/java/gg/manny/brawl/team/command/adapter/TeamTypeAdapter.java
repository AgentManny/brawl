package gg.manny.brawl.team.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TeamTypeAdapter implements CommandTypeAdapter<Team> {

    private final Brawl plugin;

    @Override
    public Team transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equalsIgnoreCase("self")) {
            Team team = plugin.getTeamHandler().getTeamByPlayer((Player)sender);
            if (team == null) {
                sender.sendMessage(CC.RED + "You are not in a team.");
            }
            return team;
        }
        Team team = plugin.getTeamHandler().getTeam(source);
        if (team == null) {
            team = plugin.getTeamHandler().getTeamByPlayer(source);
        }

        if (team == null) {
            sender.sendMessage(CC.RED + "Team " + source + " not found.");
        }
        return team;
    }

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Team team : plugin.getTeamHandler().getTeams()) {
            if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
                completions.add(team.getName());
            }
        }
        return completions;
    }
}
