package rip.thecraft.brawl.util.exception;

public class PlayerNotFoundException extends Throwable {

    private String player;

    public PlayerNotFoundException(String player) {
        super(player + " can't be found!");
    }

}
