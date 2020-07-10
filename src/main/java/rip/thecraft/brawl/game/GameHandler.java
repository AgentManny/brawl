package rip.thecraft.brawl.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.game.map.GameMapHandler;
import rip.thecraft.brawl.game.type.*;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.MovementHandler;

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
    private Map<GameType, Long> cooldown = new HashMap<>();

    public GameHandler(Brawl brawl) {
        this.brawl = brawl;

        this.mapHandler = new GameMapHandler(this);

        Arrays.asList(
                new Spleef(),
                new Sumo(),
                new FFA(),
                new Tag(),
                new OITC(),
                new WoolShuffle(),
                new Brackets()
        ).forEach(this::register);
    }

    private void register(Game game) {
        this.games.put(game.getType(), game);

        if (game instanceof Listener) {
            brawl.getServer().getPluginManager().registerEvents((Listener) game, brawl);
        } else if (game instanceof MovementHandler) {
            CraftServer.getInstance().addMovementHandler((MovementHandler) game);
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
