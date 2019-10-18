package gg.manny.brawl.duelarena.arena;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import gg.manny.brawl.util.LocationSerializer;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class Arena {

    private final String name;
    private ArenaType arenaType;

    private boolean playable = true;
    private boolean enabled = true;

    private Location[] locations;

    public static Arena create(String name, ArenaType type, Location firstLocation, Location secondLocation) {
        Arena arena = new Arena(name);
        arena.setArenaType(type);
        arena.setLocations(new Location[]{firstLocation, secondLocation});
        return arena;
    }

    public BasicDBObject toJson() {
        BasicDBList locations = new BasicDBList();
        for (Location location : this.locations) {
            locations.add(LocationSerializer.serialize(location));
        }

        return new BasicDBObject("name", name)
                .append("type", arenaType.name())
                .append("enabled", enabled)
                .append("locations", locations);
    }

    public Arena(BasicDBObject object) {
        this.name = object.getString("name");
        this.arenaType = ArenaType.valueOf(object.getString("type"));

        this.enabled = object.getBoolean("enabled");

        BasicDBList locations = (BasicDBList) object.get("locations");
        this.locations = locations.stream().map(obj -> LocationSerializer.deserialize((BasicDBObject) obj)).collect(Collectors.toList()).toArray(new Location[] { });
    }

    public FancyMessage getFancyDisplay() {
        FancyMessage fancy = new FancyMessage(CC.LIGHT_PURPLE + name);


        fancy.color(ChatColor.WHITE);
        fancy.then(" - ");

        Location firstLoc = locations[0];
        Location secondLoc = locations[1];

        fancy.color(ChatColor.YELLOW);
        fancy.then("(" + firstLoc.getBlockX() + ", " + firstLoc.getBlockY() + ", " + firstLoc.getBlockZ() + ")");
        fancy.command("/tppos " + firstLoc.getX() + " " + firstLoc.getY() + " " + firstLoc.getZ());
        fancy.tooltip(ChatColor.AQUA + "Teleport to first location!");

        fancy.color(ChatColor.LIGHT_PURPLE);
        fancy.then(", ");

        fancy.color(ChatColor.YELLOW);
        fancy.then("(" + secondLoc.getBlockX() + ", " + secondLoc.getBlockY() + ", " + secondLoc.getBlockZ() + ")");
        fancy.command("/tppos " + secondLoc.getX() + " " + secondLoc.getY() + " " + secondLoc.getZ());
        fancy.tooltip(ChatColor.AQUA + "Teleport to second location!");

        return fancy;
    }

    @Override
    public String toString() {
        Location firstLoc = locations[0];
        Location secondLoc = locations[1];
        String[] friendlyLocs = new String[]{"(" + firstLoc.getBlockX() + ", " + firstLoc.getBlockY() + ", " + firstLoc.getBlockZ() + ")", "(" + secondLoc.getBlockX() + ", " + secondLoc.getBlockY() + ", " + secondLoc.getBlockZ() + ")"};
        return "name=" + name + ";type=" + arenaType.name() + ";locations=[" + friendlyLocs[0] + "," + friendlyLocs[1] + "]";
    }

}