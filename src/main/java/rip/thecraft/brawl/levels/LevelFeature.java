package rip.thecraft.brawl.levels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LevelFeature {

    /** Allows you to view detailed statistics about your performance and others */
    STATS("Player Stats",5),

    /** Access to challenges to help gain more experience and credits  */
    CHALLENGES("Challenges", 10),

    /** Allows you to use /refill anywhere in warzone (costing credits) */
    REFILL("Refill",15),

    /** Access to bounties, allowing you to bounty other players
     *
     * Prestige would lower the cost tax on bounties (and cost more to get bountied)
     */
    BOUNTY("Bounty", 15),

    /** Allows you to use /repair anywhere in warzone (costing credits)
     *
     * Prestige would lower the cost (and eventually make it free with a cooldown)
     */
    REPAIR("Repair", 20),

    /**
     * Allows you to upgrade features for Warzone
     * Increased multipliers for: EXP, Credits
     *
     * Maybe: Allow soups to heal more hearts or give effects?
     *
     */
    UPGRADES("Upgrades", 25),

    /**
     * Access to customizable killstreaks
     */
    KILLSTREAKS("Killstreaks", 30),

    /** Access to perks
     *
     * Prestige would (give
     */
    PERKS("Perks", 45),

    /** Access to a private vault where you can store Persistent items (e.g. Killstreak items) */
    PRIVATE_VAULT("Private Vaults", 50),

    /** Access to prestiging menu that allows you to reset stats and other features */
    PRESTIGE("Prestige", 100);

    private String name;
    private int level;

}
