package rip.thecraft.brawl.team.command.staff;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class ForceNameCommand {

    @Command(names = "forcename", permission = "brawl.team.forcename")
    public void execute(Player sender, @Param(defaultValue= "self") Team team, String name) {
        sender.sendMessage(CC.GRAY + "Renamed team '" + team.getName() + "' to " + name + ".");
        team.rename(name);
    }
}
