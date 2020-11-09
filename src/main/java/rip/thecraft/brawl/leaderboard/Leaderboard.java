package rip.thecraft.brawl.leaderboard;

import com.google.gson.internal.LinkedTreeMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import org.bson.Document;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.player.statistic.StatisticType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
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
        for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
            eloLeaderboards.put(loadout, data(loadout));
        }

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
                PlayerData playerData = AquaCore.INSTANCE.getAquaCoreAPI().getPlayerData(UUID.fromString(doc.getString("uuid")));
                statistics.put(playerData == null ? doc.getString("username") : playerData.getNameColor(), statDocument.getDouble(statisticType.name()));
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

            PlayerData playerData = AquaCore.INSTANCE.getAquaCoreAPI().getPlayerData(UUID.fromString(doc.getString("uuid")));
            statistics.put(playerData == null ? doc.getString("username") : playerData.getNameColor(), elo);
        }
        return statistics;
    }

}
