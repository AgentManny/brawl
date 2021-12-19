package rip.thecraft.brawl.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.lang.Nullable;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class EventHandler {

    public static long EVENT_COOLDOWN_TIME = TimeUnit.MINUTES.toMillis(10);
    private long eventCooldown = -1;

    private final Brawl plugin;

    private final Multimap<EventType, Event> events = ArrayListMultimap.create();
    @Nullable @Setter private Event activeEvent;

    public EventHandler(Brawl plugin) {
        this.plugin = plugin;
        try {
            this.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        event.setup();
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
        }.runTaskTimer(Brawl.getInstance(), 20L, event.getUpdateInterval()));
        event.start();
        activeEvent = event;
     }

     public Event getEvent(EventType type, String name) {
         Collection<Event> events = this.events.get(type);
         if (events == null || events.isEmpty()) return null;

         for (Event event : events) {
             if (event.name.equalsIgnoreCase(name)) {
                 return event;
             }
         }
         return null;
     }

    /**
     * Loads events from disk
     */
    public void load() throws Exception {
        plugin.getLogger().info("[Event Handler] Loading events...");
        File file = getFile();
        @Cleanup FileReader reader = new FileReader(file);
        JsonElement element = new JsonParser().parse(reader);
        if (element != null && element.isJsonObject()) {
            String json = element.toString();
            Document document = Document.parse(json);

            for (EventType type : EventType.values()) {
                if (document.containsKey(type.name())) {
                    List<Document> events = document.getList(type.name(), Document.class);
                    if (events != null) {
                        for (Document eventDoc : events) {
                            Class<? extends Event> eventClazz = type.getRegistry();
                            Event event = eventClazz.getConstructor(String.class).newInstance(eventDoc.getString("name"));
                            if (eventDoc.containsKey("properties")) {
                                Document properties = eventDoc.get("properties", Document.class);
                                if (!properties.isEmpty()) {
                                    event.deserializeProperties(properties);
                                }
                            }
                            this.events.put(type, event);
                        }
                    }
                }
            }
        }
        plugin.getLogger().info("[Event Handler] Loaded " + events.size() + " events...");
        save();
    }

    private Document serialize() {
        Document eventData = new Document();
        for (EventType type : EventType.values()) {
            List<Document> events = new ArrayList<>();
            if (this.events.containsKey(type)) {
                for (Event event : this.events.get(type)) {
                    events.add(event.serialize());
                }
            }
            eventData.put(type.name(), events);
        }
        return eventData;
    }

    /**
     * Saves events to disk
     */
    public void save() {
        Document events = serialize();
        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            String json = events.toJson(JsonWriterSettings.builder()
                    .indent(true)
                    .outputMode(JsonMode.SHELL)
                    .build());
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "events.json");
        if (!file.exists()) {
            try {
                plugin.getLogger().info("[Event Handler] Creating events.json file...");
                file.createNewFile();

            } catch (IOException ignored) {
            }
        }
        return file;
    }

}
