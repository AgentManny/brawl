package gg.manny.brawl.game.map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.manny.pivot.serialization.LocationAdapter;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.Map;
import java.util.TreeMap;

@Data
@RequiredArgsConstructor
public class GameMap {

    private final String name;

    @NonNull
    private TreeMap<String, Location> locations = new TreeMap<>();

    public GameMap(JsonObject object) {
        this.name = object.get("name").getAsString();

        if (object.has("locations")) {
            JsonObject locations = object.get("locations").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : locations.entrySet()) {
                this.locations.put(entry.getKey(), LocationAdapter.fromJson(entry.getValue()));
            }
        }
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("name", this.name);

        JsonObject locations = new JsonObject();
        for (Map.Entry<String, Location> entry : this.locations.entrySet()) {
            locations.add(entry.getKey(), LocationAdapter.toJson(entry.getValue()));
        }

        object.add("locations", locations);
        return object;
    }

}
