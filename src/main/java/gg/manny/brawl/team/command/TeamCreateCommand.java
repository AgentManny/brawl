package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.team.Team;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamCreateCommand {

    private final Brawl brawl;

    @Command(names = { "team create", "t create", "faction create", "f create" })
    public void create(Player sender, String name) {
        if (brawl.getTeamHandler().getTeamByPlayer(sender) != null) {
            sender.sendMessage(Locale.TEAM_ERROR_PLAYER_FOUND.format());
            return;
        }

        if (BrawlUtil.ALPHA_NUMERIC_PATTERN.matcher(name).find()) {
            sender.sendMessage(Locale.TEAM_ERROR_ALPHA_NUMERIC.format());
            return;
        }

        if (name.length() > 12) {
            sender.sendMessage(Locale.TEAM_ERROR_NAME_MAX.format());
            return;
        }

        if (name.length() < 3) {
            sender.sendMessage(Locale.TEAM_ERROR_NAME_MIN.format());
            return;
        }

        if (brawl.getTeamHandler().getTeam(name) != null) {
            sender.sendMessage(Locale.TEAM_ERROR_ALREADY_EXISTS.format(name));
            return;
        }

        Team team = new Team(name, sender.getUniqueId());
        brawl.getTeamHandler().create(team, true);
        sender.sendMessage(Locale.TEAM_CREATE.format());
        Pivot.getPlugin().getNametagHandler().reloadPlayer(sender);
    }
}
