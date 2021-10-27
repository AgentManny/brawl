package rip.thecraft.brawl.game;

import lombok.Getter;

@Getter
public enum GameElimination {

    WATER,
    PLAYER,
    DEATH,
    FALL("fell from a high place"),
    OTHER("gave up"),
    LEFT("left"),
    QUIT("disconnected");

    private String message;

    GameElimination(String message) {
        this.message = message;
    }

    GameElimination() {
        this.message = "has been eliminated";
    }

}