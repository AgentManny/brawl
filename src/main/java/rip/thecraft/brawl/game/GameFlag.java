package rip.thecraft.brawl.game;

public enum GameFlag {

    /** Falling in water will eliminate */
    WATER_ELIMINATE,

    /** Dying to a player will eliminate */
    PLAYER_ELIMINATE,

    /** Taking fall damage will eliminate */
    FALL_ELIMINATE, // Should fall damage eliminate you

    /** Should inventory interaction be disabled */
    DISABLE_INTERACTING,

    /** Should crafting be enabled */
    CRAFTING,

    /** Should hunger be enabled */
    HUNGER,

    /** All inflicted damage will be set to 0 */
    NO_DAMAGE,

    /** Should you be able to damage during grace period */
    ALLOW_DAMAGE_GRACE,

    /** Should fall damage be disabled */
    NO_FALL,

    /** Should PVP be disabled */
    NO_PVP,

    /** Will add DoulbeJump Ability to the game*/
    DOUBLE_JUMP

}
