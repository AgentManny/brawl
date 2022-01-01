package rip.thecraft.brawl.spawn.team.command.info;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class TeamInfoCommand {

    @Command(names = { "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i" })
    public static void displayInfo(Player sender,@Param(defaultValue = "self") Team team) {
        team.sendTeamInfo(sender);
    }

}
