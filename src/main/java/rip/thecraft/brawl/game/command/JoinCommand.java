package rip.thecraft.brawl.game.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class JoinCommand {
    
    @Command(names = "join", description = "Join an active event")
    public static void execute(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (Brawl.getInstance().getGameHandler().getActiveGame() != null) {
            sender.sendMessage(CC.RED + "Game has already started.");
            return;
        }

        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        if (lobby == null) {
            sender.sendMessage(ChatColor.RED + "There isn't any games joinable right now.");
            return;
        }

        if(StaffMode.hasStaffMode(sender)){
            sender.sendMessage(ChatColor.RED + "You cannot join events while in staff mode.");
            return;
        }

        if ((playerData.isSpawnProtection() || playerData.isDuelArena()) && !playerData.isEvent() && !lobby.getPlayers().contains(sender.getUniqueId())) {
            lobby.join(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to join events.");
        }
    }

    @Command(names = "game forcejoin", permission = "op")
    public static void execute(Player sender, PlayerData target) {
        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Game already started.");
            return;
        }

        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        if (lobby == null) {
            sender.sendMessage(ChatColor.RED + "No game lobby found.");
            return;
        }

        if (target.isSpawnProtection() && !target.isEvent() && !lobby.getPlayers().contains(target.getUuid())) {
            lobby.join(target.getPlayer());
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to join events.");
        }
    }

}