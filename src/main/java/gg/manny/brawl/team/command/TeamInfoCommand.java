package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TeamInfoCommand {

    private final Brawl brawl;

    @Command(names = { "team info", "t info", "faction info", "f info", "team who", "t who", "faction who", "f who" }, async = true)
    public void execute(CommandSender sender, @Parameter(value = "self") Team team) {
        team.sendTeamInfo(sender);
    }

}