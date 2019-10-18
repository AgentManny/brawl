package gg.manny.brawl.team.command.staff;

import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import gg.manny.server.util.chatcolor.CC;
import org.bukkit.entity.Player;

public class ForceNameCommand {

    @Command(names = "forcename", permission = "brawl.team.forcename")
    public void execute(Player sender, @Parameter(value= "self") Team team, String name) {
        sender.sendMessage(CC.GRAY + "Renamed team '" + team.getName() + "' to " + name + ".");
        team.rename(name);
    }
}
