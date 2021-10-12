package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.Player;

import java.util.Map;

public interface AbilityScoreboardHandler extends AbilityHandler {

    /**
     * Prints additional data to scoreboard
     * @param player Player viewing scoreboard
     */
    Map<String, String> getScoreboard(Player player);

}
