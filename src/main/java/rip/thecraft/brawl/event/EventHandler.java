package rip.thecraft.brawl.event;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.king.KillTheKing;
import rip.thecraft.brawl.event.koth.KOTH;
import rip.thecraft.spartan.Spartan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class EventHandler {

    private static long EVENT_COOLDOWN_TIME = TimeUnit.MINUTES.toMillis(10);
    private long eventCooldown = -1;

    private final List<Event> events = new ArrayList<>();
    @Nullable private Event activeEvent;

    private final Map<String, KOTH> KOTHS = new HashMap<>();
    private final Map<String, KillTheKing> KINGS = new HashMap<>();

    private KillTheKing currentKingGame = null;

    @Setter
    private KOTH activeKOTH;

    public EventHandler() {
        this.load();
    }

    public void start(Event event, Player hoster) {
        String errorMessage = null;
        if (activeEvent != null) {
            errorMessage = "there's already an active event";
        } else if (!event.isSetup()) {
            errorMessage = "the event isn't setup";
        } else if ((hoster != null && !hoster.isOp()) && System.currentTimeMillis() - eventCooldown < EVENT_COOLDOWN_TIME) {
            errorMessage = "there is a cooldown";
        }

        if (errorMessage != null) {
            if (hoster != null) {
                hoster.sendMessage(ChatColor.RED + "You can't host " + event.getDisplayName() + ChatColor.RED + " as " + errorMessage + "!");
            }
            return;
        }

        event.broadcast(false, event.getBroadcastMessage(hoster));
        event.setActiveTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (activeEvent == null) {
                    event.setActiveTask(null);
                    cancel();
                } else {
                    event.tick();
                }
            }
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L));
        event.start();
        activeEvent = event;
     }

    public void load() {
        try {
            File file = getFile();
            String payload = FileUtils.readFileToString(file);

            if (!payload.isEmpty()) {
                BasicDBObject data = BasicDBObject.parse(payload);

                BasicDBList kings = (BasicDBList) data.get("kings");
                if (kings != null) {
                    for (Object object : kings) {
                        BasicDBObject dbo = (BasicDBObject) object;
                        KillTheKing king = this.createKING(dbo.getString("name"));
                        king.deserialize(dbo);
                    }
                }

                BasicDBList koths = (BasicDBList) data.get("koths");
                if (koths != null) {
                    for (Object object : koths) {
                        BasicDBObject dbo = (BasicDBObject) object;
                        KOTH koth = this.createKOTH(dbo.getString("name"));
                        koth.deserialize(dbo);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            File file = getFile();

            BasicDBList koths = new BasicDBList();
            KOTHS.values().forEach(k -> koths.add(k.serialize()));

            BasicDBList kings = new BasicDBList();
            KINGS.values().forEach(k -> kings.add(k.serialize()));

            FileUtils.write(file, Spartan.GSON.toJson(new JsonParser().parse(new BasicDBObject("koths", koths)
                    .append("kings", kings).toString())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public KOTH createKOTH(String name) {
        KOTH koth = new KOTH(name);
        KOTHS.put(name, koth);
        return koth;
    }

    public KillTheKing createKING(String name) {
        KillTheKing killTheKing = new KillTheKing(name);
        KINGS.put(name, killTheKing);
        return killTheKing;
    }

    public KOTH getKOTHByName(String name) {
        for (KOTH koth : KOTHS.values()) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return koth;
            }
        }
        return KOTHS.get(name);
    }

    public KillTheKing getKINGbyName(String name) {
        for (KillTheKing king : KINGS.values()) {
            if (king.getName().equalsIgnoreCase(name)) {
                return king;
            }
        }
        return KINGS.get(name);
    }

    private File getFile() throws IOException {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "events.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

}
