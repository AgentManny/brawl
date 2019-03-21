package gg.manny.brawl.game;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.lobby.GameLobby;
import gg.manny.brawl.game.map.GameMapHandler;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class GameHandler {

    private final Brawl brawl;

    private final GameMapHandler mapHandler;

    private GameLobby lobby;

    public GameHandler(Brawl brawl) {
        this.brawl = brawl;

        this.mapHandler = new GameMapHandler(this);
    }

    public void save() {
        this.mapHandler.save();
    }

    public void start(Player hoster, GameType type) {

    }
}
