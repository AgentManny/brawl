package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.team.Team;
import gg.manny.brawl.team.TeamRole;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamDemoteCommand {

    private final Brawl brawl;

    @Command(names = { "team demote", "t demote", "faction demote", "f demote" }, async = true /* PlayerData cached by redis for offline players */)
    public void execute(Player sender, PlayerData targetData) {
        PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(sender);
        if (playerData.equals(targetData)) {
            sender.sendMessage(Locale.TEAM_ERROR_PLAYER_SELF.format());
            return;
        }

        Team playerTeam = brawl.getTeamHandler().getTeamByPlayer(sender);
        Team team = brawl.getTeamHandler().getTeamByPlayerData(targetData);

        if (playerTeam != team && !sender.isOp()) {
            sender.sendMessage(Locale.TEAM_ERROR_DIFFERENT_TEAM.format(targetData.getName()));
            return;
        }

        if (!team.getRole(targetData.getUniqueId()).hasRole(TeamRole.COLEADER)) {
            sender.sendMessage(Locale.TEAM_ERROR_LEADER_ONLY.format());
            return;
        }

        TeamRole role = team.getRole(targetData.getUniqueId());
    }

}