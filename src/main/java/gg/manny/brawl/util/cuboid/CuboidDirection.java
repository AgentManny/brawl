package gg.manny.brawl.util.cuboid;

/**
 * Represents directions that can be applied to certain faces and actions of a Cuboid
 */
public enum CuboidDirection {

    NORTH,
    EAST,
    SOUTH,
    WEST,
    UP,
    DOWN,
    HORIZONTAL,
    VERTICAL,
    BOTH,
    UNKNOWN;

    public CuboidDirection opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case HORIZONTAL:
                return VERTICAL;
            case VERTICAL:
                return HORIZONTAL;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case BOTH:
                return BOTH;
            default:
                return UNKNOWN;
        }
    }
}