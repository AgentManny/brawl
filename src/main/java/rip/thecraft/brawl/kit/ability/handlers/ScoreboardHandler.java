package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;

import java.util.Map;

public interface ScoreboardHandler extends AbilityHandler {

    /**
     * Prints additional data to scoreboard
     * @param player Player viewing scoreboard
     */
    Map<String, String> getScoreboard(Player player);

}
