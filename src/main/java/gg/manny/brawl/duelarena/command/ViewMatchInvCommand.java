package gg.manny.brawl.duelarena.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.match.MatchSnapshot;
import gg.manny.brawl.duelarena.menu.PostMatchDataMenu;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ViewMatchInvCommand {

    @Command(names = "viewmatchinv")
    public static void execute(Player sender, String matchId, String playerId) {
        MatchSnapshot snapshot = Brawl.getInstance().getMatchHandler().getSnapshotMap().get(matchId);
        if (snapshot == null) {
            sender.sendMessage(ChatColor.RED + "Match not found.");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(playerId);
        } catch (Exception e) {
            uuid = null;
        }

        if (uuid != null && snapshot.getInventories().containsKey(uuid)) {
            new PostMatchDataMenu(SimpleOfflinePlayer.getNameByUuid(uuid), snapshot, snapshot.getInventories().get(uuid)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Inventory not found.");
        }

    }
}