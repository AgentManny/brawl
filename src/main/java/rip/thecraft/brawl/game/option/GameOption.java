package rip.thecraft.brawl.game.option;

import rip.thecraft.brawl.game.Game;

public interface GameOption {

    /**
     * Should this game option be voteable during the
     * game lobby
     *
     * @return Whether Voting should be enabled
     */
    default boolean voting() {
        return true;
    }

    default void onStart(Game game) {

    }

    default void onEnd(Game game) {

    }

}
