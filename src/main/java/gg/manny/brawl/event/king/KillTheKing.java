package gg.manny.brawl.event.king;

import com.mongodb.BasicDBObject;
import gg.manny.brawl.event.Event;
import gg.manny.brawl.util.LocationSerializer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class KillTheKing extends Event {

    private final String name;
    private Location location;

    private int startsIn = 45;

    @Override
    public void start(Player host) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.DARK_PURPLE + host.getDisplayName() + ChatColor.YELLOW + " is hosting " + ChatColor.DARK_PURPLE + "Kill The King" + ChatColor.YELLOW + " event in 30 seconds.");
            player.sendMessage(ChatColor.YELLOW + "To participate as a king type /join");
            player.sendMessage(" ");
        }
    }

    @Override
    public void finish(Player winner) {

    }

    @Override
    public BasicDBObject serialize() {
        return new BasicDBObject("name", this.name)
                .append("location", location == null ? null : LocationSerializer.serialize(location));
    }

    @Override
    public void deserialize(BasicDBObject object) {
        if (object.get("location") != null) {
            this.location = LocationSerializer.deserialize((BasicDBObject) object.get("location"));
        }
    }
}
