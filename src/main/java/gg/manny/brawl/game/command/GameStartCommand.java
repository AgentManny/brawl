package gg.manny.brawl.game.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.lobby.GameLobby;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameStartCommand {

    @Command(names = "game start", permission = "op")
    public void execute(Player sender, GameType type) {
        sender.sendMessage(ChatColor.GREEN + "Starting " + type.getName() + " (" + type.getShortName() + ")");
    }

    @Command(names = "game forcestart", permission = "op")
    public void execute(Player sender) {
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
        lobby.setStartTime(1);
        sender.sendMessage(ChatColor.GREEN + "Starting " + lobby.getGameType().getName() + ".");
    }

}
