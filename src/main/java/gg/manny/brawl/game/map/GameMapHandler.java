package gg.manny.brawl.game.map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.GameHandler;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.command.GameMapCommand;
import gg.manny.brawl.game.command.adapter.GameTypeAdapter;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.Quantum;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMapHandler {

    private final GameHandler handler;

    @Getter
    private Multimap<GameType, GameMap> gameMap = ArrayListMultimap.create();

    public GameMapHandler(GameHandler handler) {
        this.handler = handler;

        Quantum quantum = Pivot.getPlugin().getQuantum();
        quantum.registerParameterType(GameType.class, new GameTypeAdapter());
        quantum.registerCommand(new GameMapCommand(handler.getBrawl()));

        this.load();
    }

    public Collection<GameMap> getMaps(GameType gameType) {
        return this.gameMap.get(gameType);
    }

    public GameMap getMapByName(GameType gameType, String name) {
        Collection<GameMap> maps = this.gameMap.get(gameType);
        return maps.stream()
                .filter(gameMap -> gameMap.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))
                .findAny()
                .orElse(null);
    }

    public GameMap createMap(GameType gameType, String name) {
        GameMap map = new GameMap(name);
        this.gameMap.get(gameType).add(map);
        return map;
    }

    public void removeMap(GameType gameType, GameMap map) {
        this.gameMap.remove(gameType, map);
    }

    public List<String> getRequiredLocations(GameType gameType, GameMap map) {
        return gameType.getRequiredLocations().stream()
                .filter(map.getLocations().keySet()::contains)
                .collect(Collectors.toList());
    }

    private void load() {
        File file = this.getFile();
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element != null && element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (Object o : array) {
                    JsonObject object = (JsonObject) o;
                    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                        GameType type = GameType.valueOf(entry.getKey());
                        JsonObject gameMap = entry.getValue().getAsJsonObject();
                        for (Map.Entry<String, JsonElement> mapEntry : gameMap.entrySet()) {
                            GameMap map = new GameMap(mapEntry.getValue().getAsJsonObject());
                            this.gameMap.get(type).add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File file = this.getFile();
        try (FileWriter writer = new FileWriter(file)) {

            JsonArray array = new JsonArray();
            for (GameType gameType : GameType.values()) {
                Collection<GameMap> maps = this.gameMap.get(gameType);
                JsonObject object = new JsonObject();
                JsonObject mapObject = new JsonObject();
                for (GameMap map : maps) {
                    mapObject.add(map.getName(), map.toJson());
                }
                object.add(gameType.name(), mapObject);
                array.add(object);
            }

            Pivot.GSON.toJson(array, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "maps.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
