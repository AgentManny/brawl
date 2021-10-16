package rip.thecraft.brawl.game.games;

import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.type.BracketsGame;

public class Sumo extends BracketsGame {

    public Sumo() {
        super(GameType.SUMO, null);

        this.flags.add(GameFlag.WATER_ELIMINATE);
        this.flags.add(GameFlag.NO_DAMAGE);
    }

    @Override
    public int getRefillAmount() {
        return 0;
    }
}
