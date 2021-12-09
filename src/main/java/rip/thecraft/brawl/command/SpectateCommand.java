package rip.thecraft.brawl.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.util.VisibilityUtils;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class SpectateCommand {

    @Command(names = { "spec", "spectate" })
    public static void spectate(Player player, @Param(defaultValue = "self") Player target) {
//        if (!player.isOp()) {
//            player.sendMessage(ChatColor.RED + "Spectating is currently disabled. Please try again later.");
//            player.sendMessage(ChatColor.GRAY + "Note: We are still working on integrating Spectating for the variety of games we plan to introduce.");
//            return;
//        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        SpectatorManager sm = Brawl.getInstance().getSpectatorManager();
        SpectatorMode spectator = sm.getSpectator(player);
        if (spectator == null) {
            if (!player.hasPermission("brawl.spectate")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return;
            }
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

    @Command(names = "visibility", permission = "op")
    public static void vdebug(Player player, Player observer, Player target) {
        player.sendMessage(ChatColor.GREEN + "Visibility Logic (Spectator)");
        player.sendMessage(ChatColor.GRAY + "(" + observer.getName() + " -> " + target.getName() + "): " + (VisibilityUtils.shouldSee(observer, target) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(ChatColor.GRAY + "(" + target.getName() + " -> " + observer.getName() + "): " + (VisibilityUtils.shouldSee(target, observer) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(" ");
        player.sendMessage(ChatColor.DARK_PURPLE + "Visibility Test (Spectator)");
        player.sendMessage(ChatColor.GRAY + "(" + observer.getName() + " -> " + target.getName() + "): " + (Brawl.getInstance().getEntityHider().canSee(observer, target) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(ChatColor.GRAY + "(" + target.getName() + " -> " + observer.getName() + "): " + (Brawl.getInstance().getEntityHider().canSee(target, observer) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(" ");
        player.sendMessage(ChatColor.DARK_PURPLE + "Visibility Test (Match)");
        player.sendMessage(ChatColor.GRAY + "(" + observer.getName() + " -> " + target.getName() + "): " + (observer.canSee(target) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(ChatColor.GRAY + "(" + target.getName() + " -> " + observer.getName() + "): " + (target.canSee(observer) ? ChatColor.GREEN + "Visible" : ChatColor.RED + "Hidden"));
        player.sendMessage(" ");
    }

    @Command(names = "visibility update", permission = "op")
    public static void visibilityUpdate(Player player, Player observer) {
        VisibilityUtils.updateVisibility(observer);
        player.sendMessage("Updated visibility of " + observer.getName());
    }


    @Command(names = "visibility fix", permission = "op")
    public static void visibility(Player player, Player observer) {
        if (observer.hasMetadata("hidden")) {
            player.sendMessage(ChatColor.RED + "Player is in staff mode.");
            return;
        }
        if (Brawl.getInstance().getSpectatorManager().isSpectating(observer)) {
            player.sendMessage(ChatColor.RED + "Player is spectating...");
            return;
        }
        Match match = null;
        if ((match = Brawl.getInstance().getMatchHandler().getMatch(observer)) != null) {
            if (match.getArena().getArenaType() == ArenaType.ARCADE || match.getArena().getArenaType() == ArenaType.SUMO) {
                player.sendMessage(ChatColor.RED + "Player is in a match that causes hidden visibility.");
                return;
            }
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (Brawl.getInstance().getEntityHider().canSee(onlinePlayer, observer)) {
                onlinePlayer.showPlayer(observer);
            } else {
                Brawl.getInstance().getEntityHider().showEntity(onlinePlayer, observer);
            }
        }
        player.sendMessage("Updated (force) visibility of " + observer.getName());
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
        player.sendMessage();
    }

}
