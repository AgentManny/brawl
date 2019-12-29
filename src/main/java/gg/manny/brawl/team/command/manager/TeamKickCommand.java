package gg.manny.brawl.team.command.manager;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.scoreboard.NametagAdapter;
import gg.manny.brawl.team.Team;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamKickCommand {

    @Command(names = { "team kick", "t kick", "f kick", "faction kick", "fac kick" })
    public void execute(Player sender, @Parameter(value = "player") SimpleOfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        UUID uuid = offlinePlayer.getUuid();

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
                        pl.sendMessage(ChatColor.DARK_AQUA + offlinePlayer.getName() + " was kicked by " + sender.getName() + "!");
                    }
                }
                if (team.removeMember(uuid)) {
                    team.disband();
                } else {
                    team.flagForSave();
                }
                Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(uuid);
                if (player != null) {

                    Pivot.getInstance().getNametagHandler().reloadPlayer(player);
                    Pivot.getInstance().getNametagHandler().reloadOthersFor(player);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a team manager (or above) to kick players.");
        }
    }
}
