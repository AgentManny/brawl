package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamAcceptCommand {

    private final Brawl brawl;

    @Command(names = { "team accept", "t accept", "faction accept", "f accept", "team join", "t join", "faction join", "f join" })
    public void accept(Player sender, Team team) {
        Team playerTeam = brawl.getTeamHandler().getTeamByUuid(sender.getUniqueId());
        if (playerTeam != null) {
            sender.sendMessage(Locale.TEAM_ERROR_PLAYER_FOUND.format());
            return;
        }

        int size = brawl.getMainConfig().getInteger("TEAM.SIZE");
        if(team.getPlayers().size() > size && !sender.isOp()) {
            sender.sendMessage(Locale.TEAM_FULL.format(size));
            return;
        }

        if (!team.getInvitations().contains(sender.getUniqueId()) && !sender.isOp()) {
            sender.sendMessage(Locale.TEAM_ERROR_NOT_INVITED.format());
            return;
        }

        team.getInvitations().remove(sender.getUniqueId());
        team.getMembers().add(sender.getUniqueId());
        team.broadcast(Locale.TEAM_JOIN.format(sender.getName()));

    }
}
