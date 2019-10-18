package gg.manny.brawl.game.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.map.GameMap;
import gg.manny.brawl.game.map.GameMapHandler;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command(names = "game map", permission = "op")
public class GameMapCommand {

    private final Brawl brawl;

    @Command(names = "create", permission = "op")
    public void create(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) != null) {
            sender.sendMessage(ChatColor.RED + "Map " + mapName + ChatColor.RED + " for " + gameType.getName() + ChatColor.RED + " already exists.");
            return;
        }

        GameMap map = mapHandler.createMap(gameType, mapName);
        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        sender.sendMessage(ChatColor.GREEN + "Created map " + ChatColor.BOLD + mapName + ChatColor.GREEN + " for " + gameType.getName() + ChatColor.GREEN + ".");
        this.locationsLeft(sender, locations);
    }

    @Command(names = "remove", permission = "op")
    public void remove(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        mapHandler.removeMap(gameType, mapHandler.getMapByName(gameType, mapName));
        sender.sendMessage(ChatColor.RED + "Removed map " + mapName + " for " + gameType + ".");
    }

    @Command(names = { "addlocation", "addloc" }, permission = "op")
    public void addLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if (!gameType.isRandomLocations() && !gameType.getRequiredLocations().contains(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            this.locationsLeft(sender, locations);
        }

        map.getLocations().put(locationName, sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Added location " + ChatColor.WHITE + map.getName() + ChatColor.GREEN + " for " + gameType.getName() + " game.");
        this.locationsLeft(sender, locations);
    }

    @Command(names = { "removelocation", "removeloc" }, permission = "op")
    public void removeLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            this.locationsLeft(sender, locations);
            return;
        }

        map.getLocations().remove(locationName, sender.getLocation());
        sender.sendMessage(ChatColor.RED + "Removed location " + ChatColor.WHITE + map.getName() + ChatColor.RED + " for " + gameType.getName() + " game.");
        this.locationsLeft(sender, locations);
    }

    @Command(names = { "teleport", "tp" }, permission = "op")
    public void teleport(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            this.locationsLeft(sender, locations);
            return;

        }

        sender.sendMessage(ChatColor.GREEN + "Teleported to " + locationName + " for " + map.getName() + " " + gameType.getName() + ".");
        sender.teleport(map.getLocations().get(locationName));
    }

    @Command(names = { "listlocations", "listlocs" }, permission = "op")
    public void listLocs(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = brawl.getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        locationsLeft(sender, map.getLocations().keySet());
    }

    @Command(names = "list", permission = "op")
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

    @Command(names = "save", permission = "op")
    public void save(Player sender) {
        long time = System.currentTimeMillis();
        brawl.getGameHandler().getMapHandler().save();
        sender.sendMessage(CC.GREEN + "Saved in " + (System.currentTimeMillis() - time) + "ms.");
    }

    @Command(names = { "requiredlocations", "requiredlocs" }, permission = "op")
    public void requiredLocation(Player sender, GameType gameType) {
        locationsLeft(sender, gameType.getRequiredLocations());
    }

    private void locationsLeft(CommandSender sender, List<String> locations) {
        sender.sendMessage(ChatColor.GRAY + "Location remaining (" + locations.size() + "): " + ChatColor.WHITE + (locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

    private void locationsLeft(CommandSender sender, Set<String> locations) {
        sender.sendMessage(ChatColor.GRAY + "Location remaining (" + locations.size() + "): " + ChatColor.WHITE + (locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

}
