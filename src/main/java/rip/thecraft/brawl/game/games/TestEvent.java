package rip.thecraft.brawl.game.games;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.type.RoundGame;

import java.util.List;

public class TestEvent extends RoundGame {

    public TestEvent() {
        super(GameType.THIMBLE, GameFlag.FALL_ELIMINATE);

        this.newRoundDelay = 20L;
    }

    @Override
    public void onRoundStart() {
        // Create new platform
    }

    @Override
    public void onRoundEnd() {

    }

    @Override
    public List<String> getSidebar(Player player) {
        return super.getSidebar(player);
    }
}
