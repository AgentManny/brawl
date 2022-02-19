package rip.thecraft.brawl.spawn.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum ChallengeType {

    /** Challenge increments by credits earned */
    CREDITS(Material.DOUBLE_PLANT),

    /** Challenge increments by experience earned */
    EXPERIENCE(Material.EXP_BOTTLE),

    /** Challenge increments by contracts completed */
    CONTRACT(Material.EMPTY_MAP), // TODO:

    /** Challenge increments by player kills */
    KILLS(Material.REDSTONE),

    /** Challenge increments by killstreak */
    KILLSTREAK(Material.STONE_SWORD),

    /** Challenge increments when an ability is used */
    ABILITY(Material.GLOWSTONE_DUST), // TODO:

    /** Challenge increments by playing games  */
    GAMES(Material.EYE_OF_ENDER),

    /** Challenge increments by winning a game */
    GAME_WINS(Material.FIREWORK),

    /** Challenge increment by playing in the duel arena */
    DUELS(Material.IRON_SWORD),

    /** Challenges increment by winning in the duel arena */
    DUEL_WINS(Material.DIAMOND_SWORD),

    ;

    private Material icon;

    public String getDisplayName() {
        return WordUtils.capitalizeFully(name().toLowerCase().replace("_", " "));
    }

}
