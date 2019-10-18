package gg.manny.brawl.team.command.staff;

import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names = "forceleader", permission = "brawl.team.forceleader")
    public void execute(Player sender, @Parameter(value= "self") Team team, @Parameter(value = "self") String target) {
        UUID uuid;
        if (target.equals("self")) {
            uuid = sender.getUniqueId();
        } else {
            uuid = SimpleOfflinePlayer.getUuidByName(target);
        }

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
        } else {
            if (!team.isMember(uuid)) {
                sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
                return;
            }
            team.setOwner(uuid);
            sender.sendMessage(ChatColor.GREEN + target + " is now the owner of §b" + team.getName());
        }
    }
}
