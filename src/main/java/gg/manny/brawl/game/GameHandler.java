package gg.manny.brawl.game;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.command.GameMapCommand;
import gg.manny.brawl.game.command.GameStartCommand;
import gg.manny.brawl.game.command.HostCommand;
import gg.manny.brawl.game.command.JoinCommand;
import gg.manny.brawl.game.command.adapter.GameTypeAdapter;
import gg.manny.brawl.game.lobby.GameLobby;
import gg.manny.brawl.game.map.GameMapHandler;
import gg.manny.brawl.game.type.*;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.Quantum;
import gg.manny.server.MineServer;
import gg.manny.server.handler.SimpleMovementHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GameHandler {

    private final Brawl brawl;

    private final GameMapHandler mapHandler;

    @Setter private Game activeGame;
    @Setter private GameLobby lobby;

    private Map<GameType, Game> games = new HashMap<>();

    public GameHandler(Brawl brawl) {
        this.brawl = brawl;

        this.mapHandler = new GameMapHandler(this);

        Arrays.asList(
                new Spleef(),
                new Sumo(),
                new FFA(),
                new Tag(),
                new OITC()
        ).forEach(this::register);

        Quantum quantum = Pivot.getInstance().getQuantum();
        quantum.registerParameterType(GameType.class, new GameTypeAdapter());
        Arrays.asList(new GameMapCommand(brawl), new HostCommand(brawl), new JoinCommand(brawl), new GameStartCommand())
        .forEach(quantum::registerCommand);
    }

    private void register(Game game) {
        this.games.put(game.getType(), game);

        if (game instanceof Listener) {
            brawl.getServer().getPluginManager().registerEvents((Listener) game, brawl);
        } else if (game instanceof SimpleMovementHandler) {
            MineServer.getInstance().addMovementHandler((SimpleMovementHandler) game);
        }
    }

    public void save() {
        this.mapHandler.save();
    }

    public void start(Player hoster, GameType type) {
        if (lobby != null || activeGame != null) {
            if (hoster != null) {
                hoster.sendMessage(ChatColor.RED + "There is already an ongoing game active.");
            }
            return;
        }
        this.lobby = new GameLobby(brawl, type);
        Bukkit.broadcastMessage(Game.PREFIX + ChatColor.WHITE + (hoster == null ? "Someone" : hoster.getDisplayName()) + ChatColor.YELLOW + " is hosting the " + ChatColor.DARK_PURPLE + type.getShortName() + ChatColor.YELLOW + " event for a prize of " + ChatColor.LIGHT_PURPLE + "250 credits" + ChatColor.YELLOW + ".");
    }

    public void destroy() {
        if (activeGame == null) return;

        activeGame.cleanup();

        activeGame = null;
        lobby = null;

    }
}
