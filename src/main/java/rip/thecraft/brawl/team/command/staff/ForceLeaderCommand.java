package rip.thecraft.brawl.team.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names = "forceleader", permission = "brawl.team.forceleader")
    public void execute(Player sender, @Param(defaultValue= "self") Team team, @Param(defaultValue = "self")CacheProfile cacheProfile) {
        UUID uuid = cacheProfile.getUuid();

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
        } else {
            if (!team.isMember(uuid)) {
                sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
                return;
            }
            team.setOwner(uuid);
            sender.sendMessage(ChatColor.GREEN + cacheProfile.getUsername() + " is now the owner of §b" + team.getName());
        }
    }
}
