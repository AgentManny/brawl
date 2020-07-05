package rip.thecraft.brawl.util.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import rip.thecraft.brawl.Brawl;

@AllArgsConstructor
public enum LocationType {

    SPAWN("SPAWN"),
    ARENA("DUEL_ARENA"),

    HOLOGRAM_STATS("HOLO_STATS"),
    HOLOGRAM_LEADERBOARDS("HOLO_LB"),

    UPGRADER("NPC_UPGRADER"),
    CHALLENGES("NPC_CHALLENGES");

    @Getter private final String name;

    public Location getLocation() {
        return Brawl.getInstance().getLocationByName(name);
    }

    public static LocationType parse(String source) {
        for (LocationType location : values()) {
            if (location.name().equalsIgnoreCase(source) || location.name.equalsIgnoreCase(source)) {
                return location;
            }
        }
        return null;
    }

}
