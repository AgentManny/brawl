package rip.thecraft.brawl.game.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class JoinCommand {
    
    @Command(names = "join", description = "Join an active event")
    public static void execute(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (Brawl.getInstance().getGameHandler().getActiveGame() != null) {
            sender.sendMessage(CC.RED + "Event has already started.");
            return;
        }

        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        if (lobby == null) {
            sender.sendMessage(ChatColor.RED + "There isn't any games joinable right now.");
            return;
        }

        if (playerData.isSpawnProtection() && !playerData.isEvent() && !lobby.getPlayers().contains(sender.getUniqueId())) {
            lobby.join(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to join events.");
        }
    }

}