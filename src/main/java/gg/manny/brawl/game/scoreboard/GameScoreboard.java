package gg.manny.brawl.game.scoreboard;

import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameState;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.server.util.chatcolor.CC;

import java.util.ArrayList;
import java.util.List;

/**
 * This was intended to forcefully set a scoreboard as
 * configurable scoreboards aren't set.
 *
 * Further plans to create profile system scoreboards (@link Game)
 */
@Deprecated
public class GameScoreboard {

    private static String PRIMARILY_COLOUR = CC.DARK_PURPLE;
    private static String SECONDARY_COLOUR = CC.LIGHT_PURPLE;
    private static String WARNING_COLOUR = CC.RED;

    private GameScoreboard() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static List<String> getDefault(Game game) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(PRIMARILY_COLOUR + "Event: " + SECONDARY_COLOUR + game.getType().getShortName());
        toReturn.add(PRIMARILY_COLOUR + "Players: " + SECONDARY_COLOUR + game.getAlivePlayers().size() + "/" + game.getPlayers().size());
        toReturn.addAll(getState(game));
        return toReturn;
    }

    private static List<String> getState(Game game) {
        List<String> toReturn = new ArrayList<>();
        if (game.getState() == GameState.GRACE_PERIOD) {
            toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);

            long seconds = game.getTime();

            toReturn.add(PRIMARILY_COLOUR + "Starting in " + SECONDARY_COLOUR + seconds + "s");
        } else if (game.getState() == GameState.FINISHED) {
            toReturn.add(CC.GREEN + CC.STRIKETHROUGH);
            boolean winners = game.getWinners().size() > 1;
            if (winners) {
                toReturn.add(PRIMARILY_COLOUR + "Winners: ");
                for (GamePlayer player : game.getWinners()) {
                    toReturn.add(SECONDARY_COLOUR + "  " + player.getName());
                }
            } else if (!game.getWinners().isEmpty()) {
                toReturn.add(PRIMARILY_COLOUR + "Winner: " + SECONDARY_COLOUR + game.getWinners().get(0).getName());
            } else {
                toReturn.add(PRIMARILY_COLOUR + "Winner: " + WARNING_COLOUR + "None");
            }
        }
        return toReturn;
    }
}