package rip.thecraft.brawl.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.falcon.command.player.SettingsCommand;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerDataHandler implements Closeable {

    private Brawl plugin;

    private Map<UUID, PlayerData> playerMap = new ConcurrentHashMap<>();

    private MongoCollection<Document> mongoCollection;

    public PlayerDataHandler(Brawl plugin) {
        this.plugin = plugin;

        this.mongoCollection = plugin.getMongoDatabase().getCollection("playerData");
        SettingsCommand.SETTINGS.addAll(Arrays.asList(
                SettingsCommand.Settings.create("killstreak messages", "Toggle receiving killstreak messages in chat.", ChatColor.RED, Material.IRON_SWORD, "togglekillstreaks", (player -> Brawl.getInstance().getPlayerDataHandler().getPlayerData(player).isKillstreakMessages())),
                SettingsCommand.Settings.create("game messages", "Toggle receiving game messages in chat", ChatColor.GREEN, Material.WATCH, "togglegame", (player -> Brawl.getInstance().getPlayerDataHandler().getPlayerData(player).isGameMessages()))
        ));
    }

    public int save(boolean forceAll) {
        System.out.println("Saving players to Mongo...");
        int saved = 0;
        long startMs = System.currentTimeMillis();

        for (PlayerData playerData : playerMap.values()) {
            if (playerData.isNeedsSaving() || forceAll) {
                saved++;

                playerData.setNeedsSaving(false);
                playerData.save();
            }
        }

        int time = (int) (System.currentTimeMillis() - startMs);
        if (saved > 0) {
            Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + "Updated " + saved + " players (Completed: " + CC.YELLOW + time + "ms" + CC.LIGHT_PURPLE + ")");
            System.out.println("Saved " + saved + " players to Mongo in " + time + "ms.");
        }
        return saved;
    }


    public PlayerData create(PlayerData playerData, boolean cache) {
        if (!cache) {
            this.playerMap.put(playerData.getUuid(), playerData);
            playerData.load();
        }
        return playerData;
    }

    public void remove(PlayerData profile) {
        this.playerMap.values().removeIf(p -> p.equals(profile));
    }

    public Document getDocument(UUID uniqueId) {
        return this.mongoCollection.find(Filters.eq("uuid", uniqueId.toString())).first();
    }

    public void setDocument(Document document, UUID uniqueId) {
        this.mongoCollection.replaceOne(Filters.eq("uuid", uniqueId.toString()), document, new ReplaceOptions().upsert(true));
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
        } else if (MUUIDCache.uuid(name) != null) {
            profile = new PlayerData(MUUIDCache.uuid(name), name);
            System.out.println("[PlayerData - Offline | Primary: " + Bukkit.isPrimaryThread() + "] Loading " + name + " (" + profile.getUuid().toString() + ") data");
            profile.fromDocument(plugin.getPlayerDataHandler().getDocument(profile.getUuid()));
        }
        return profile;
    }

    @Override
    public void close() {
        this.playerMap.values().forEach(PlayerData::save);
    }

}
