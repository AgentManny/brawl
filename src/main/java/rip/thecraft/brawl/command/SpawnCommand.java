package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.nametag.NametagHandler;

public class SpawnCommand {

    @Command(names = "spawn")
    public static void execute(Player sender) {
        Location spawn = LocationType.SPAWN.getLocation();
        if(spawn == null) {
            sender.sendMessage(ChatColor.RED + "Uh oh! It looks like the Spawn hasn't been set. Please contact an administrator.");
            return;
        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isEvent()) {
            GameHandler gh =  Brawl.getInstance().getGameHandler();
            SpectatorManager sm =  Brawl.getInstance().getSpectatorManager();
            if (sm.isSpectating(sender)) {
                sm.removeSpectator(sender);
            }

            if (gh.getLobby() != null && gh.getLobby().getPlayers().contains(sender.getUniqueId())) {
                gh.getLobby().leave(sender.getUniqueId());
                sender.sendMessage(ChatColor.RED + "You have left the event.");
            }

            Game game = gh.getActiveGame();
            if (game != null) {
                if (game.containsPlayer(sender)) {
                    GamePlayer gamePlayer = game.getGamePlayer(sender);
                    if (gamePlayer.isAlive()) {
                        game.handleElimination(sender, sender.getLocation(), GameElimination.LEFT);
                    }
                }

                NametagHandler.reloadPlayer(sender);
                NametagHandler.reloadOthersFor(sender);
            }
            return;
        }

        SpectatorManager sm =  Brawl.getInstance().getSpectatorManager();
        if (sm.isSpectating(sender)) {
//            sm.getSpectator(sender).spectate(SpectatorMode.SpectatorType.SPAWN);
            sm.getSpectator(sender).leave(); // Remove them instead
            return;
        }

        if (playerData.isDuelArena()) {
            DuelArena.leave(sender);
            return;
        }

        if (sender.getGameMode() != GameMode.CREATIVE) {
            if (!sender.isOnGround()) {
                sender.sendMessage(ChatColor.RED + "You need to be on the ground to warp to spawn.");
                return;
            }

            if (playerData.hasCombatLogged()) {
                sender.sendMessage(ChatColor.RED + "You can't teleport to spawn while in combat.");
                return;
            }
        }


        playerData.warp("spawn", spawn, BrawlUtil.getNearbyPlayers(sender, 25).isEmpty() ? 3 : 5, playerData::spawn);
    }

    @Command(names = {"spawn force"}, permission = "falcon.command.spawn.force")
    public static void execute(Player player, Player target){
        Location spawn = LocationType.SPAWN.getLocation();
        if(spawn == null) {
            player.sendMessage(ChatColor.RED + "Uh oh! It looks like the Spawn hasn't been set. Please contact an administrator.");
            return;
        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);
        if (playerData.isEvent()) {
            GameHandler gh =  Brawl.getInstance().getGameHandler();
            SpectatorManager sm =  Brawl.getInstance().getSpectatorManager();
            if (sm.isSpectating(target)) {
                sm.removeSpectator(target);
            }

            if (gh.getLobby() != null && gh.getLobby().getPlayers().contains(target.getUniqueId())) {
                gh.getLobby().leave(target.getUniqueId());
                target.sendMessage(ChatColor.RED + "You have left the event.");
            }

            Game game = gh.getActiveGame();
            if (game != null) {
                if (game.containsPlayer(target)) {
                    GamePlayer gamePlayer = game.getGamePlayer(target);
                    if (gamePlayer.isAlive()) {
                        game.handleElimination(target, target.getLocation(), GameElimination.LEFT);
                    }
                }

                NametagHandler.reloadPlayer(target);
                NametagHandler.reloadOthersFor(target);
            }
            return;
        }

        if (playerData.isDuelArena()) {
            DuelArena.leave(target);
            return;
        }

        playerData.warp("spawn", spawn, 0, playerData::spawn);
        player.sendMessage(ChatColor.YELLOW + "You have forced " + ChatColor.DARK_PURPLE + target.getName() + ChatColor.YELLOW + " to spawn.");
    }

    @Command(names = { "1v1", "1vs1", "duelarena"})
    public static void duelArena(Player sender) {
        DuelArena.join(sender);
    }

}
