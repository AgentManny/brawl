package rip.thecraft.brawl.event.control;

import com.mongodb.BasicDBObject;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.region.Region;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.brawl.util.imagemessage.ImageMessage;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlPoint extends Event {

    public static final String PREFIX = CC.DARK_PURPLE + "[Control Point] " + CC.WHITE;

    private final String name;

    public ControlPoint(String name) {
        this.name = name;
    }

    @Setter
    private Cuboid captureZone;

    private Map<String, Integer> points = new HashMap<>();

    private BukkitTask task;

    @Override
    public void start(Player host) {
        Location centerLoc = captureZone.getCenter();
        Region region = Brawl.getInstance().getRegionHandler().get(centerLoc);
        String locationName = region == null ? "" : WordUtils.capitalize(region.getName().replace("_", " ")) + ChatColor.GRAY + " ";

        String[] messages = new String[] {
                "",
                "&5&lCONTROL POINT",
                "&7Try to take control of the platform located at (&f100, 200&7)"
        };
        List<String> message = new ArrayList<>();
        message.add(" ");
        message.add(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "CONTROL POINT");

        new ImageMessage("controlpoint").appendText(" ", " ", PREFIX, ChatColor.WHITE + name, ChatColor.YELLOW + "can be contested now.").broadcast();

    }

    @Override
    public void finish(Player winner) {

    }

    @Override
    public BasicDBObject serialize() {
        return null;
    }

    @Override
    public void deserialize(BasicDBObject object) {

    }
}
