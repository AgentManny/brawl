package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamDisbandCommand {

    private final Brawl brawl;

    @Command(names = { "team disband", "t disband", "faction disband", "f disband" })
    public void disband(Player sender, @Parameter(value = "self") Team team) {
        Team playerTeam = brawl.getTeamHandler().getTeamByUuid(sender.getUniqueId());
        if (playerTeam == null) {
            sender.sendMessage(Locale.TEAM_ERROR_PLAYER_NOT_FOUND.format());
            return;
        }

        if (playerTeam != team && !sender.isOp()) {
            sender.sendMessage(Locale.TEAM_ERROR_DISBAND.format());
            return;
        }

        if(!team.getLeader().equals(sender.getUniqueId())) {
            sender.sendMessage(Locale.TEAM_ERROR_LEADER_ONLY.format());
            return;
        }

        brawl.getTeamHandler().remove(team);
        team.broadcast(Locale.TEAM_DISBAND.format(sender.getName()));

    }
}
