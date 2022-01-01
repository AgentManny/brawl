package rip.thecraft.brawl.util.location;

import gg.manny.streamline.npc.NPC;
import gg.manny.streamline.npc.NPCRegistry;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;

import java.util.function.BiConsumer;

public enum LocationType {

    SPAWN("SPAWN"),
    GAME_LOBBY("GAME_LOBBY"),
    ARENA("DUEL_ARENA"),

    HOLOGRAM_STATS("HOLO_STATS"),
    HOLOGRAM_LEADERBOARDS("HOLO_LB"),
    HOLOGRAM_LEADERBOARDS_ELO("HOLO_STATS_ELO"),

    UPGRADER("NPC_UPGRADER", (player, location) -> {
        for (NPC npc : NPCRegistry.getNpcs()) {
            if (npc.getName().toLowerCase().contains("upgrader")) {
                npc.teleport(location);
            }
        }
    }),

    CHALLENGES("NPC_CHALLENGES");

    @Getter private final String name;
    @Getter private BiConsumer<Player, Location> update = null;

    LocationType(String name) {
        this.name = name;
    }

    LocationType(String name, BiConsumer<Player, Location> update) {
        this.name = name;
        this.update = update;
    }


    public Location getLocation() {
        return Brawl.getInstance().getLocationByName(name);
    }

    public static LocationType parse(String value) {
        String source = value.replace(" ", "_");
        for (LocationType location : values()) {
            if (location.name().equalsIgnoreCase(source) || location.name.equalsIgnoreCase(source)) {
                return location;
            }
        }
        return null;
    }

}
