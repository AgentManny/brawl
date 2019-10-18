package gg.manny.brawl.team.command.info;

import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import org.bukkit.entity.Player;

public class TeamInfoCommand {

    @Command(names = { "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i" })
    public void execute(Player sender,@Parameter(value = "self") Team team) {
        team.sendTeamInfo(sender);
    }

}
