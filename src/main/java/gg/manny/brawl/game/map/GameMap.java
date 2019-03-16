package gg.manny.brawl.game.map;

import com.google.gson.JsonObject;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.Pivot;
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

    public GameMap(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.locations = Pivot.GSON.fromJson(object.get("locations").getAsString(), BrawlUtil.MAP_STRING_LOCATION);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("name", this.name);

        object.addProperty("locations", Pivot.GSON.toJson(this.locations, BrawlUtil.MAP_STRING_LOCATION));
        return object;
    }

}
