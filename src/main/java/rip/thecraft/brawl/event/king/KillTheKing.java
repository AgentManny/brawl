package rip.thecraft.brawl.event.king;

import com.mongodb.BasicDBObject;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.king.runnables.WaitingCountdownRunnable;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.util.LocationSerializer;
import rip.thecraft.brawl.util.MathUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class KillTheKing extends Event {

    private final Brawl plugin = Brawl.getInstance();
    private final String name;
    private Location location;
    private GameState state = GameState.GRACE_PERIOD;
    private List<UUID> players = new ArrayList<>();
    private UUID kingUUID;
    private String hostDisplayName;

    private int startsIn = 45;

    @Override
    public void start(Player host) {
        plugin.getServer()
                .broadcastMessage(" " +
                        "\n " + ChatColor.DARK_PURPLE + host.getDisplayName() + ChatColor.YELLOW + " is hosting " + ChatColor.DARK_PURPLE + "Kill The King" + ChatColor.YELLOW + " event in 1 minute." +
                        "\n " + ChatColor.YELLOW + "To participate as a king type /join" +
                        "\n ");
        this.hostDisplayName = host.getDisplayName();
        players.add(host.getUniqueId());
        new WaitingCountdownRunnable(this).runTaskTimer(plugin, 1L, 20L);
    }

    @Override
    public void finish(Player winner) {

    }

    /***
     * Ran when the waiting countdown for the game is done.
     */
    public void runSetup() {
        removeOfflinePlayers();
        if(players.size() < 2) {
            for(UUID uuid : players) {
                Player player = plugin.getServer().getPlayer(uuid);
                player.sendMessage(ChatColor.DARK_PURPLE + "The Kill The King event has been " + ChatColor.DARK_RED + " Cancelled" +
                        "\n " + ChatColor.DARK_PURPLE + "Due to the lack of players.");
            }
            // TODO: Cancel Event...
            return;
        }
        Collections.shuffle(players);
        this.kingUUID = players.get(MathUtil.getRandomInt(0, players.size()));
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private void removeOfflinePlayers() {
        List<UUID> onlinePlayers = new ArrayList<>(players);
        for(int i = 0; i < players.size(); i++) {
            if(plugin.getServer().getPlayer(players.get(i)) == null)
                onlinePlayers.remove(i);
        }
        players = onlinePlayers;
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