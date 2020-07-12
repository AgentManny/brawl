package rip.thecraft.brawl.game.type;

import lombok.Getter;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.game.team.GameTeam;

import java.util.ArrayList;
import java.util.List;

public abstract class PartneredGame extends Game {

    private List<GameTeam<GamePlayer>> teams = new ArrayList<>();
    private List<GameTeam<GamePlayer>> alreadyPlayed = new ArrayList<>();

    @Getter private GameTeam teamOne;
    @Getter private GameTeam teamTwo;

    public PartneredGame(GameType type) {
        super(type);
    }

    public GameTeam<GamePlayer> getTeam(Player player) {
        for (GameTeam<GamePlayer> team : teams) {
            if (team.containsPlayer(player)) {
                return team;
            }
        }
        return null;
    }
}
