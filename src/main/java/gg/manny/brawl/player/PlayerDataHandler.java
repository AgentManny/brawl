package gg.manny.brawl.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.adapter.PlayerDataTypeAdapter;
import gg.manny.pivot.Pivot;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class PlayerDataHandler implements Closeable {

    private Brawl plugin;

    private Map<UUID, PlayerData> playerMap = new ConcurrentHashMap<>();

    private MongoCollection<Document> mongoCollection;

    public PlayerDataHandler(Brawl plugin) {
        this.plugin = plugin;

        this.plugin.getQuantum().registerParameterType(PlayerData.class, new PlayerDataTypeAdapter(plugin));

        this.mongoCollection = plugin.getMongoDatabase().getCollection("playerData");

        new ClearCacheTask().runTaskTimer(plugin, 20L, TimeUnit.MINUTES.toMillis(5L));
    }

    public PlayerData create(PlayerData playerData, boolean cache) {
        if (!cache) {
            this.playerMap.put(playerData.getUniqueId(), playerData);
        }
        return playerData;
    }

    public void remove(PlayerData profile) {
        this.playerMap.values().removeIf(p -> p.equals(profile));
    }

    public Document getDocument(UUID uniqueId) {
        return this.mongoCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();
    }

    public void setDocument(Document document, UUID uniqueId) {
        this.mongoCollection.replaceOne(Filters.eq("uniqueId", uniqueId.toString()), document, new ReplaceOptions().upsert(true));
    }

    public PlayerData getPlayerData(UUID uniqueId) {
        return this.playerMap.get(uniqueId);
    }

    public PlayerData getPlayerData(Player player) {
        return this.getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(String name) {
        PlayerData profile = this.playerMap.values().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny().orElse(null);
        if (profile != null) {
            return profile;
        }
        Player target = Bukkit.getPlayer(name);
        if (target != null) {
            profile = this.getPlayerData(target.getUniqueId());
        } else if (Pivot.getPlugin().getUuidCache().getUuid(name) != null){
            profile = new PlayerData(Pivot.getPlugin().getUuidCache().getUuid(name), name);
        }
        return profile;
    }

    public class ClearCacheTask extends BukkitRunnable {

        @Override
        public void run() {
            playerMap.values().stream().filter(playerData -> playerData.toPlayer() == null).forEach(playerData -> {
                remove(playerData);
                System.out.println("Removed Profile '" + playerData.toString() + "' as player is not online.");
            });
        }
    }

    @Override
    public void close() {
        this.playerMap.values().forEach(PlayerData::save);
    }

}
