package rip.thecraft.brawl.spawn.event;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.mongodb.BasicDBObject;
import com.mongodb.lang.Nullable;
import gg.manny.streamline.util.ColorUtil;
import gg.manny.streamline.util.PlayerUtils;
import gg.manny.streamline.util.cuboid.Cuboid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.github.paperspigot.Title;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.kit.ability.property.codec.Codec;
import rip.thecraft.brawl.kit.ability.property.codec.Codecs;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.util.LocationSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class Event {

    @Getter protected final String name;
    @Getter protected final EventType type;

    protected long startedAt = -1L;

    @Setter private BukkitTask activeTask;

    protected List<Player> winners = new ArrayList<>();

    public Event(Document data) {
        this.name = data.getString("name");
        this.type = EventType.valueOf(data.getString("type"));

        if (data.containsKey("properties")) {
            Document properties = data.get("properties", Document.class);
            if (properties != null) {
                deserializeProperties(properties);
            }
        }
    }

    public void onSpawnLeave(Player player, PlayerData playerData) {

    }

    public void setup() {
        this.startedAt = System.currentTimeMillis();
    }

    public abstract void start();

    public void finish(Player winner) {
        for (int i = 0; i < 4; i++) {
            Bukkit.broadcastMessage(" ");
        }
    }

    public void getScoreboard(Player player, List<String> entries) {
        entries.add(" - " + getDisplayName() + ChatColor.RESET + " - ");
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

        // Remove any values we don't want stored in memory anymore
        for (Field field : getClass().getFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                try {
                    field.set(this, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        LCWaypoint waypoint = getWaypoint(false);
        if (waypoint != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PlayerUtils.onLegacyVersion(player)) continue;
                LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
            }
        }

        Brawl.getInstance().getEventHandler().setActiveEvent(null);
    }

    protected LCWaypoint getWaypoint(boolean visibility) {
        Location location = getLocation();
        if (location == null) return null;
        int color = ColorUtil.toDyeColor(type.getColor()).getColor().asRGB();
        return new LCWaypoint(name, location, color, false, visibility);
    }


    public long getUpdateInterval() {
        return 20L;
    }

    public abstract void tick();

    /** Returns whether or not the event has everything completed in order to start */
    public abstract boolean isSetup();

    public String[] getBroadcastMessage(@Nullable Player hoster) {
        Location location = getLocation();
        return new String[] {
                "",
                type.getColor().toString() + ChatColor.BOLD + type.getDisplayName(),
                ChatColor.GRAY + type.getDescription(),
                " ",
                ChatColor.WHITE + "Event: " + ChatColor.LIGHT_PURPLE + name,
                location == null ? "" : ChatColor.WHITE + "Location: " + ChatColor.LIGHT_PURPLE + "(" + location.toVector().toString() + ")",
                ""
        };
    }

    public String getDisplayName() {
        return type.getColor() + type.getDisplayName();
    }

    public boolean isMapsRequired() {
        return true;
    }

    /**
     * Location of the event, will return null if no location is displayed.
     * @return Location of event
     */
    public Location getLocation() {
        return null;
    }

    public Document serialize() {
        return new Document("name", name)
                .append("properties", serializeProperties());
    }

    private Document serializeProperties() {
        Document properties = new Document();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    String id = property.id().isEmpty() ? field.getName().toLowerCase() : property.id();
                    Class<?> type = field.getType();
                    Object value = field.get(this);
                    Codec<?> codec = Codecs.getCodecByClass(type);
                    if (value == null) continue;
                    properties.put(id, codec != null ? codec.encode(value) :
                            type.isAssignableFrom(Location.class) ? LocationSerializer.serialize((Location) value) :
                            type.isAssignableFrom(Cuboid.class) ? ((Cuboid)value).toDocument() :
                                    value
                    );
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    protected void deserializeProperties(Document properties) {
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    String id = property.id().isEmpty() ? field.getName().toLowerCase() : property.id();
                    if (properties.containsKey(id)) {
                        Class<?> type = field.getType();
                        Codec<?> codec = Codecs.getCodecByClass(type);
                        Object value =
                                type.isAssignableFrom(Location.class) ? LocationSerializer.deserialize(BasicDBObject.parse(properties.get(id, Document.class).toJson())) :
                                type.isAssignableFrom(Cuboid.class) ? new Cuboid(properties.get(id, Document.class)) :
                                        codec != null ? codec.decode(properties.getString(id)) : properties.get(id);
                        field.set(this, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getSetupRequirements() {
        if (isSetup()) return null; // Event is already setup
        List<String> requirements = new ArrayList<>();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    String id = property.id().isEmpty() ? field.getName().toLowerCase() : property.id();
                    String friendlyName = WordUtils.capitalizeFully(id.replace("/([A-Z])/g", " $1").trim()
                            .replace("_", " ")
                            .replace("-", " "));
                    requirements.add(friendlyName + " (" + field.getType().getSimpleName() + ")");
                }
            } catch (Exception ignored) {

            }
        }
        return requirements;
    }

    /**
     * Get properties of abilities that are configurable
     * @return Event properties
     */
    public Map<String, Field> getProperties() {
        Map<String, Field> properties = new HashMap<>();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    String id = property.id().isEmpty() ? field.getName().toLowerCase() : property.id();
                    properties.put(id, field);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public void action(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.onLegacyVersion(player)) continue;
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection() || playerData.getPlayerState() == PlayerState.FIGHTING) {
                player.sendActionBar(message);
            }
        }
    }

    public void announce(String title, String subtitle) {
        LCWaypoint waypoint = getWaypoint(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtils.onLegacyVersion(player)) continue;
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.isSpawnProtection() || playerData.getPlayerState() == PlayerState.FIGHTING) {
                player.sendTitle(new Title(title, subtitle));
                if (waypoint != null) {
                    LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
                }
            }
        }
    }

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
