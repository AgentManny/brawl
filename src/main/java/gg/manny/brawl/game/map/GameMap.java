package gg.manny.brawl.game.map;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashMap;

@Data
@RequiredArgsConstructor
public class GameMap {

    private final String name;

    @NonNull
    private HashMap<String, Location> locations = new HashMap<>();

}
