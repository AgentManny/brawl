package rip.thecraft.brawl.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class SpectateCommand {

    @Command(names = { "spec", "spectate" })
    public static void spectate(Player player, @Param(defaultValue = "self") Player target) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Spectating is currently disabled.");
            return;
        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        SpectatorManager sm = Brawl.getInstance().getSpectatorManager();
        SpectatorMode spectator = sm.getSpectator(player);
        if (spectator == null) {
            if (!(playerData.isSpawnProtection() || playerData.isDuelArena())) {
                player.sendMessage(ChatColor.RED + "You can't use spectator mode here!");
                return;
            }

            if (target != player) {
                spectator = sm.addSpectator(player, target);
            } else {
                spectator = sm.addSpectator(player);
            }
        } else {
            sm.removeSpectator(player);
        }

    }

    @Command(names = "spec debug", permission = "op")
    public static void debug(Player player, Player observer) {
        SpectatorManager sm = Brawl.getInstance().getSpectatorManager();
        SpectatorMode spectator = sm.getSpectator(observer);
        if (spectator == null) {
            player.sendMessage(ChatColor.RED + "That player isn't spectating anyone.");
            return;
        }
        player.sendMessage(ChatColor.YELLOW + "*** Spectator Info (" + observer.getName() + ") ***");
        player.sendMessage(ChatColor.YELLOW + "Last state: " + spectator.getLastState().name());
        player.sendMessage(ChatColor.YELLOW + "State: " + spectator.getSpectating().name());
        player.sendMessage(ChatColor.YELLOW + "> Following: " + (spectator.getFollow() == null ? "None" : spectator.getFollow()));
        player.sendMessage(ChatColor.YELLOW + "> Game: " + (spectator.getGame() == null ? "None" : spectator.getGame().getType().getName()));
        player.sendMessage(ChatColor.YELLOW + "> Match: " + (spectator.getMatch() == null ? "None" : spectator.getMatch().getPlayers()[0].getName() + " vs. " + spectator.getMatch().getPlayers()[1]));;
        player.sendMessage(ChatColor.YELLOW + "> Hidden players: ");
        spectator.getHiddenPlayers().forEach(uuid -> {
            Player hiddenPlayer = Bukkit.getPlayer(uuid);
            player.sendMessage(ChatColor.YELLOW + " - " + ChatColor.WHITE + (hiddenPlayer != null ? hiddenPlayer.getName() : uuid.toString()));
        });
        player.sendMessage();
    }

}
