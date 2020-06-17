package rip.thecraft.brawl.game.command;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class JoinCommand {

    private final Brawl plugin;
    
    @Command(names = "join", description = "Join an active event")
    public void execute(Player sender) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);
        if (plugin.getGameHandler().getActiveGame() != null) {
            sender.sendMessage(CC.RED + "Event has already started.");
            return;
        }

        GameLobby lobby = plugin.getGameHandler().getLobby();
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