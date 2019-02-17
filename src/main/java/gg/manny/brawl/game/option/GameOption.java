package gg.manny.brawl.game.option;

import gg.manny.brawl.game.Game;

public interface GameOption {

    default void onStart(Game game) {

    }

    default void onEnd(Game game) {

    }

}
