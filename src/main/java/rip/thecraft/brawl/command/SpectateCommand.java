package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.spartan.command.Command;

public class SpectateCommand {

    @Command(names = { "spec", "follow", "spectate" })
    public static void spectate(Player player) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Spectating is currently disabled.");
            return;
        }

        SpectatorManager sm = Brawl.getInstance().getSpectatorManager();
        SpectatorMode spectator = sm.getSpectator(player);
        if (spectator == null) {
            spectator = sm.addSpectator(player);
            player.sendMessage("Added: ");
        } else {
            sm.removeSpectator(player);
            player.sendMessage("rEMOVED ");
            return;
        }

    }

    @Command(names = "spec debug", permission = "op")
    public static void debug(Player player) {
        SpectatorManager sm = Brawl.getInstance().getSpectatorManager();
        SpectatorMode spectator = sm.getSpectator(player);
        player.sendMessage(ChatColor.YELLOW + "*** Spectator Debug ***");
        player.sendMessage(ChatColor.GRAY + "(Legacy state: " + sm.getSpectating(player).name() + ")");
        player.sendMessage(ChatColor.YELLOW + "Active: " + (spectator != null));
        if (spectator != null) {
            player.sendMessage(ChatColor.YELLOW + "Last state: " + spectator.getLastState().name());
            player.sendMessage(ChatColor.YELLOW + "State: " + spectator.getSpectating().name());
            player.sendMessage(ChatColor.YELLOW + "> Following: " + (spectator.getFollow() == null ? "None" : spectator.getFollow()));
            player.sendMessage(ChatColor.YELLOW + "> Game: " + (spectator.getGame() == null ? "None" : spectator.getGame().getType().getName()));
            player.sendMessage(ChatColor.YELLOW + "> Match: " + (spectator.getMatch() == null ? "None" : spectator.getMatch().getPlayers()[0].getName() + " vs. " + spectator.getMatch().getPlayers()[1]));
        };
        player.sendMessage();
    }

}
