package rip.thecraft.brawl.event;

import com.mongodb.BasicDBObject;
import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class Event {

    protected final String name;
    protected final EventType type;

    @Setter private BukkitTask activeTask;

    protected List<Player> winners = new ArrayList<>();

    public abstract void start();

    public void finish(Player winner) {
        for (int i = 0; i < 4; i++) {
            Bukkit.broadcastMessage(" ");
        }
    }

    public void getScoreboard(Player player, List<String> entries) {
    }

    public void end() {
        if (activeTask != null) {
            activeTask.cancel();
        }

        for (Player winner : winners) {
            if (winner != null) {
                type.getRewards().forEach((reward, value) -> reward.addRewards(winner, value));
            }
        }
    }

    public abstract void tick();

    /** Returns whether or not the event has everything completed in order to start */
    public abstract boolean isSetup();

    public String[] getBroadcastMessage(@Nullable Player hoster) {
        return new String[] {
                "",
                type.getColor().toString() + ChatColor.BOLD + name.toUpperCase(),
                ChatColor.GRAY + type.getDescription(),
                ""
        };
    }

    public String getDisplayName() {
        return type.getColor() + type.getDisplayName();
    }

    /**
     * Location of the event, will return null if no location is displayed.
     * @return Location of event
     */
    public Location getLocation() {
        return null;
    }

    public abstract BasicDBObject serialize();

    public abstract void deserialize(BasicDBObject object);

    public void broadcast(String... message) {
        broadcast(true, message);
    }

    public void broadcast(boolean prefix, String... messages) {
        String prefixName = prefix ? type.getPrefix() : "";
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String message : messages) {
                player.sendMessage(prefixName + message);
            }
        }
    }
}
