package rip.thecraft.brawl.game.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.spartan.command.Command;

public class GameStartCommand {

    @Command(names = "game start", permission = "op")
    public static void execute(Player sender, GameType type) {
        sender.sendMessage(ChatColor.GREEN + "Starting " + type.getName() + " (" + type.getShortName() + ")");
    }

    @Command(names = "game forcestart", permission = "op")
    public static void execute(Player sender) {
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

    @Command(names = "game forcestop", permission = "op")
    public static void stop(Player sender) {
        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        if (lobby != null) {
            lobby.stop();
            sender.sendMessage(ChatColor.GREEN + "Forcefully stopped " + ChatColor.WHITE + lobby.getGameType().getShortName() + ChatColor.GREEN + " game lobby.");
        } else if (game != null) {
            game.end(true);
            sender.sendMessage(ChatColor.GREEN + "Forcefully stopped " + ChatColor.WHITE + game.getType().getShortName() + ChatColor.GREEN + " game.");
        } else {
            sender.sendMessage(ChatColor.RED + "No game or lobby found.");
        }
    }

    @Command(names = "game manny", permission = "op")
    public static void test(Player sender) {
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

        sender.sendMessage(ChatColor.GRAY + "Forcing " + Bukkit.getOnlinePlayers().size() + " players to join...");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            lobby.join(onlinePlayer);
        }

        lobby.setStartTime(1);
        sender.sendMessage(ChatColor.GREEN + "Starting " + lobby.getGameType().getName() + ".");
    }

}
