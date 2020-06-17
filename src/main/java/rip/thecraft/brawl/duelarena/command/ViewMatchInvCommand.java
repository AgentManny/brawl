package rip.thecraft.brawl.duelarena.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.MatchSnapshot;
import rip.thecraft.brawl.duelarena.menu.PostMatchDataMenu;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.uuid.MUUIDCache;

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
            new PostMatchDataMenu(MUUIDCache.name(uuid), snapshot, snapshot.getInventories().get(uuid)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Inventory not found.");
        }

    }
}