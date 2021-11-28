package rip.thecraft.brawl.leaderboard;

import com.google.gson.internal.LinkedTreeMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.falcon.profile.Profile;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class Leaderboard {

    private final Map<StatisticType, Map<String, Double>> spawnLeaderboards = new LinkedHashMap<>();
    private final Map<MatchLoadout, Map<String, Integer>> eloLeaderboards = new LinkedHashMap<>();

    public Leaderboard(Brawl plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::update, 20L, TimeUnit.MINUTES.toMillis(15));
    }

    public void update() {
        spawnLeaderboards.clear();
        for (StatisticType type : StatisticType.values()) {
            spawnLeaderboards.put(type, data(type));
        }

        eloLeaderboards.clear();
        // Duel arenas are disabled
//        for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
//            eloLeaderboards.put(loadout, data(loadout));
//        }

        System.out.println("Updated leaderboards");
    }

    private Map<String, Double> data(StatisticType statisticType) {
        Map<String, Double> statistics = new LinkedHashMap<>(); // Save order
        MongoCollection<Document> collection = Brawl.getInstance().getPlayerDataHandler().getMongoCollection();
        Iterator<Document> iterator = collection.find().sort(Sorts.descending("statistic.spawn." + statisticType.name())).limit(10).iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            Document statDocument = (Document) ((Document) doc.get("statistic")).get("spawn");
            if (statDocument != null) {
//                UUID uuid = UUID.fromString(doc.getString("uuid"));
//                String displayName = doc.getString("username");
//                String color = ChatColor.WHITE.toString();
//                Player player = Bukkit.getPlayer(uuid);
//                if (player != null) {
//                    displayName = player.getDisplayName();
//                }
//                statistics.put(color + displayName, statDocument.getDouble(statisticType.name()));
                statistics.put(getDisplayColor(UUID.fromString(doc.getString("uuid"))) + doc.getString("username"), statDocument.getDouble(statisticType.name()));
            }
        }
        return statistics;
    }

    private Map<String, Integer> data(MatchLoadout matchLoadout) {
        Map<String, Integer> statistics = new LinkedTreeMap<>(); // Save order
        MongoCollection<Document> collection = Brawl.getInstance().getPlayerDataHandler().getMongoCollection();
        Iterator<Document> iterator = collection.find().sort(Sorts.descending("statistic.arena." + matchLoadout.getName().toLowerCase())).limit(10).iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            Document statDoc = (Document) ((Document) doc.get("statistic")).get("arena");
            int elo = 1000;
            if (statDoc != null) {
                elo = statDoc.getInteger(matchLoadout.getName().toLowerCase(), 1000);
            }

//            Profile profile = Falcon.getInstance().getProfileHandler().loadProfile(UUID.fromString(doc.getString("uuid")));
//            statistics.put(profile.getColor() + profile.getUsername(), elo);
            statistics.put(getDisplayColor(UUID.fromString(doc.getString("uuid"))) + doc.getString("username"), elo);

        }
        return statistics;
    }


    public String getDisplayColor(UUID uuid) {
        User user = Falcon.getInstance().getLuckPerms().getUserManager().getUser(uuid);
        if (user == null) return ChatColor.WHITE.toString();

        Group primaryGroup = Falcon.getInstance().getLuckPerms().getGroupManager().getGroup(user.getPrimaryGroup());
        if (primaryGroup == null) return ChatColor.WHITE.toString();

        CachedMetaData metaData = primaryGroup.getCachedData().getMetaData();
        String playerListPrefix = metaData.getMetaValue("tab_prefix");
        if (playerListPrefix == null) {
            playerListPrefix = metaData.getMetaValue("color");
            if (playerListPrefix == null) {
                playerListPrefix = ChatColor.WHITE.toString();
            }
        }

        return ChatColor.translateAlternateColorCodes('&', playerListPrefix);
    }

}