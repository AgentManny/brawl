package rip.thecraft.brawl.game.type;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.team.GamePlayer;

import java.util.HashSet;
import java.util.Set;

public abstract class RoundGame extends Game {

    private int round = 0;
    private int maxRounds = -1; // Should game end after X amount of rounds

    private Set<GamePlayer> played = new HashSet<>(); // Players who have completed the round
    private boolean roundCheckAfterEliminate = true; // Should rounds reset after eliminate (or check isRoundOver())

    public long newRoundDelay = 30L; // in ticks

    public RoundGame(GameType type, GameFlag... flags) {
        super(type, flags);
    }

    @Override
    public void setup() {
        round = 1;
    }

    public boolean isRoundOver() {
        return played.size() >= getAlivePlayers().size();
    }

    public abstract void onRoundStart();

    public abstract void onRoundEnd();


    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        super.handleElimination(player, location, elimination);
    }
}
