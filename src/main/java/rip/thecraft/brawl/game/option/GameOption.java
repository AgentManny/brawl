package rip.thecraft.brawl.game.option;

import rip.thecraft.brawl.game.Game;

public interface GameOption {

    default void onStart(Game game) {

    }

    default void onEnd(Game game) {

    }

}
