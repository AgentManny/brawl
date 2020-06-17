package rip.thecraft.brawl.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.falcon.profile.cache.CacheProfile;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.UUID;

public class TeamKickCommand {

    @Command(names = { "team kick", "t kick", "f kick", "faction kick", "fac kick" })
    public void execute(Player sender, @Param(name = "player") CacheProfile cacheProfile) {
        Player player = cacheProfile.getPlayer();
        UUID uuid = cacheProfile.getUuid();

        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isManager(sender.getUniqueId())) {
            if (team.isMember(uuid)) {
                if (team.isOwner(uuid)) {
                    sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
                    return;
                }

                if ((team.isManager(uuid) && team.isManager(sender.getUniqueId()))) {
                    sender.sendMessage(ChatColor.RED + "You must be the team leader to kick other managers.");
                    return;
                }
                for (Player pl : Brawl.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(pl)) {
                        pl.sendMessage(ChatColor.DARK_AQUA + cacheProfile.getUsername() + " was kicked by " + sender.getName() + "!");
                    }
                }
                if (team.removeMember(uuid)) {
                    team.disband();
                } else {
                    team.flagForSave();
                }
                Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(uuid);
                if (player != null) {

                    NametagHandler.reloadPlayer(player);
                    NametagHandler.reloadOthersFor(player);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a team manager (or above) to kick players.");
        }
    }
}
