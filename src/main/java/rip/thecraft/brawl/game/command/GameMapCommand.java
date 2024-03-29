package rip.thecraft.brawl.game.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.map.GameMap;
import rip.thecraft.brawl.game.map.GameMapHandler;
import rip.thecraft.brawl.server.region.selection.Selection;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMapCommand {

    @Command(names = "game map create", permission = "op")
    public static void create(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) != null) {
            sender.sendMessage(ChatColor.RED + "Map " + mapName + ChatColor.RED + " for " + gameType.getName() + ChatColor.RED + " already exists.");
            return;
        }

        GameMap map = mapHandler.createMap(gameType, mapName);
        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        sender.sendMessage(ChatColor.GREEN + "Created map " + ChatColor.BOLD + mapName + ChatColor.GREEN + " for " + gameType.getName() + ChatColor.GREEN + ".");
        locationsLeft(sender, locations);
    }

    @Command(names = "game map remove", permission = "op")
    public static void remove(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        if (mapHandler.getMapByName(gameType, mapName) == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        mapHandler.removeMap(gameType, mapHandler.getMapByName(gameType, mapName));
        sender.sendMessage(ChatColor.RED + "Removed map " + mapName + " for " + gameType + ".");
    }

    @Command(names = { "game map addselection", "game map addsel" }, permission = "op")
    public static void addSelection(Player sender, GameType gameType, String mapName, String selectionName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        Selection selection = Selection.createOrGetSelection(sender);

        if (!selection.isFullObject()) {
            sender.sendMessage(ChatColor.RED + "You don't have a selection selected");
            return;
        }

        if (gameType.isRandomLocations()) {
            sender.sendMessage(ChatColor.RED + gameType.getShortName() + " does not support selections.");
            return;
        }

        List<String> requiredLocations = gameType.getRequiredLocations();
        for (int i = 1; i <= 2; i++) {
            String locName = selectionName + i;
            if (!requiredLocations.contains(locName)) {
                sender.sendMessage(ChatColor.RED + "Selection cannot be created as " + locName + " is not a valid location for " + gameType.getShortName() + ".");
                return;
            }
            map.getLocations().put(locName, i == 1 ? selection.getPoint1() : selection.getPoint2());
            sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.WHITE + locName + ChatColor.GREEN + " location " + ChatColor.WHITE + map.getName() + ChatColor.GREEN + " for " + gameType.getName() + " game.");
        }
        sender.sendMessage(ChatColor.GREEN + "Added selection");
    }

    @Command(names = { "game map addlocation", "game map addloc" }, permission = "op")
    public static void addLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if (!gameType.isRandomLocations() && !gameType.getRequiredLocations().contains(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            locationsLeft(sender, locations);
        }

        map.getLocations().put(locationName, sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Added location " + ChatColor.WHITE + map.getName() + ChatColor.GREEN + " for " + gameType.getName() + " game.");
        locationsLeft(sender, locations);
    }

    @Command(names = { "game map removelocation", "game map removeloc" }, permission = "op")
    public static void removeLocation(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            locationsLeft(sender, locations);
            return;
        }

        map.getLocations().remove(locationName, sender.getLocation());
        sender.sendMessage(ChatColor.RED + "Removed location " + ChatColor.WHITE + map.getName() + ChatColor.RED + " for " + gameType.getName() + " game.");
        locationsLeft(sender, locations);
    }

    @Command(names = { "game map teleport", "game map tp" }, permission = "op")
    public static void teleport(Player sender, GameType gameType, String mapName, String locationName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        List<String> locations = mapHandler.getRequiredLocations(gameType, map);

        if(!map.getLocations().containsKey(locationName)) {
            sender.sendMessage(ChatColor.RED + "Location for " + locationName + ChatColor.RED + " doesn't exist for " + gameType.getName() + " game.");
            locationsLeft(sender, locations);
            return;

        }

        sender.sendMessage(ChatColor.GREEN + "Teleported to " + locationName + " for " + map.getName() + " " + gameType.getName() + ".");
        sender.teleport(map.getLocations().get(locationName));
    }

    @Command(names = { "game map listlocations", "game map listlocs" }, permission = "op")
    public static void listLocs(Player sender, GameType gameType, String mapName) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        GameMap map = mapHandler.getMapByName(gameType, mapName);
        if (map == null) {
            sender.sendMessage(ChatColor.RED + "Game map " + mapName + " not found for " + gameType.getName() + ".");
            return;
        }

        locationsLeft(sender, map.getLocations().keySet());
    }

    @Command(names = "game map list", permission = "op")
    public static void list(Player sender) {
        GameMapHandler mapHandler = Brawl.getInstance().getGameHandler().getMapHandler();
        sender.sendMessage(CC.GREEN + "Loading all maps...");
        for (GameType type : GameType.values()) {
            Collection<String> maps = mapHandler.getMaps(type).stream()
                    .map(GameMap::getName)
                    .collect(Collectors.toList());
            sender.sendMessage(CC.GREEN + "> " + type.getName() + ": " + CC.WHITE + (maps.isEmpty() ? "None" : StringUtils.join(maps, ", ")));
        }
    }

    @Command(names = "game map save", permission = "op")
    public static void save(Player sender) {
        long time = System.currentTimeMillis();
        Brawl.getInstance().getGameHandler().getMapHandler().save();
        sender.sendMessage(CC.GREEN + "Saved in " + (System.currentTimeMillis() - time) + "ms.");
    }

    @Command(names = { "game map requiredlocations", "game map requiredlocs" }, permission = "op")
    public static void requiredLocation(Player sender, GameType gameType) {
        locationsLeft(sender, gameType.getRequiredLocations());
    }

    private static void locationsLeft(CommandSender sender, List<String> locations) {
        sender.sendMessage(ChatColor.GRAY + "Location remaining (" + locations.size() + "): " + ChatColor.WHITE + (locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

    private static void locationsLeft(CommandSender sender, Set<String> locations) {
        sender.sendMessage(ChatColor.GRAY + "Location remaining (" + locations.size() + "): " + ChatColor.WHITE + (locations.isEmpty() ? "None" : StringUtils.join(locations, ", ")));
    }

}
