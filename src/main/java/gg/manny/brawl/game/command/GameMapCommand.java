package gg.manny.brawl.game.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.map.GameMap;
import gg.manny.brawl.game.map.GameMapHandler;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameMapCommand {

    private final Brawl brawl;

    @Command(names = "game map create", permission = "op")
    public void create(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) != null) {
            Locale.GAME_MAP_ERROR_ALREADY_EXISTS.format(mapName, gameType.getName());
            return;
        }

        GameMap map = mapHandler.createMap(gameType, mapName);
        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        sender.sendMessage(Locale.GAME_MAP_CREATED.format(mapName, gameType.getName()));
        this.locationsLeft(sender, locations);
    }

    @Command(names = "game map remove", permission = "op")
    public void remove(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) == null) {
            Locale.GAME_MAP_ERROR_NOT_FOUND.format(mapName, gameType.getName());
            return;
        }

        mapHandler.removeMap(gameType, mapHandler.getMapByName(gameType, mapName));
        sender.sendMessage(Locale.GAME_MAP_REMOVED.format(mapName, gameType.getName()));
    }

    @Command(names = { "game map addlocation", "game map addloc" }, permission = "op")
    public void addLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            Locale.GAME_MAP_ERROR_NOT_FOUND.format(mapName, gameType.getName());
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if (!gameType.isRandomLocations() && !gameType.getRequiredLocations().contains(locationName)) {
            sender.sendMessage(Locale.GAME_MAP_ERROR_LOCATION_NOT_FOUND.format(locationName, gameType.getName()));
            this.locationsLeft(sender, locations);
        }

        map.getLocations().put(locationName, sender.getLocation());
        sender.sendMessage(Locale.GAME_MAP_LOCATION_ADDED.format(locationName, map.getName(), gameType.getName()));
        this.locationsLeft(sender, locations);
    }

    @Command(names = { "game map removelocation", "game map removeloc" }, permission = "op")
    public void removeLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            Locale.GAME_MAP_ERROR_NOT_FOUND.format(mapName, gameType.getName());
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(Locale.GAME_MAP_ERROR_LOCATION_NOT_FOUND.format(locationName, gameType.getName()));
            this.locationsLeft(sender, locations);
            return;
        }

        map.getLocations().remove(locationName, sender.getLocation());
        sender.sendMessage(Locale.GAME_MAP_LOCATION_REMOVED.format(locationName, map.getName(), gameType.getName()));
        this.locationsLeft(sender, locations);
    }

    @Command(names = { "game map teleport", "event map teleport" }, permission = "op")
    public void teleport(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            Locale.GAME_MAP_ERROR_NOT_FOUND.format(mapName, gameType.getName());
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(Locale.GAME_MAP_ERROR_LOCATION_NOT_FOUND.format(locationName, gameType.getName()));
            this.locationsLeft(sender, locations);
            return;

        }
        sender.sendMessage(Locale.GAME_MAP_LOCATION_TELEPORTED.format(locationName, map.getName(), gameType.getName()));
        sender.teleport(map.getLocations().get(locationName));

    }

    @Command(names = { "game map listlocations", "game map listlocs" }, permission = "op")
    public void listLocs(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            Locale.GAME_MAP_ERROR_NOT_FOUND.format(mapName, gameType.getName());
            return;
        }

        Set<String> locations = map.getLocations().keySet();
        sender.sendMessage(Locale.GAME_MAP_LOCATIONS_LEFT.format(locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

    @Command(names = "game map list", permission = "op")
    public void list(Player sender) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        sender.sendMessage(CC.GREEN + "Loading all maps...");
        for (GameType type : GameType.values()) {
            Collection<String> maps = mapHandler.getMaps(type).stream()
                    .map(GameMap::getName)
                    .collect(Collectors.toList());
            sender.sendMessage(CC.GREEN + "> " + type.getName() + ": " + CC.WHITE + (maps.isEmpty() ? "None" : StringUtils.join(maps, ", ")));
        }
    }

    @Command(names = { "game map save", "event map save" }, permission = "op")
    public void save(Player sender) {
        long time = System.currentTimeMillis();
        brawl.getGameHandler().getMapHandler().save();
        sender.sendMessage(CC.GREEN + "Saved in " + (System.currentTimeMillis() - time) + "ms.");
    }

    @Command(names = { "game map requiredlocations", "game map requiredlocs" }, permission = "op")
    public void requiredLocation(Player sender, GameType gameType) {
        List<String> locations = gameType.getRequiredLocations();
        sender.sendMessage(CC.GREEN + CC.strip(Locale.GAME_MAP_LOCATIONS_LEFT.format(CC.WHITE + (locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")))));
    }

    private void locationsLeft(CommandSender sender, List<String> locations) {
        sender.sendMessage(Locale.GAME_MAP_LOCATIONS_LEFT.format(locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

}
