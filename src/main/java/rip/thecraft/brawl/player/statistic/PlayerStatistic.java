package rip.thecraft.brawl.player.statistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.statistic.GameStatistic;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PlayerStatistic {

    private final PlayerData playerData;

    private Map<StatisticType, Double> spawnStatistics = new HashMap<>();
    private Map<String, KitStatistic> kitStatistics = new HashMap<>();
    private Map<GameType, GameStatistic> gameStatistics = new HashMap<>();

    private int globalElo = 1000;
    private Map<MatchLoadout, Integer> arenaStatistics = new HashMap<>();

    public void load(Document document) {
        if (document == null) {
            for (StatisticType statisticType : StatisticType.values()) {
                this.spawnStatistics.put(statisticType, statisticType.getDefaultValue());
            }

            for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
                arenaStatistics.put(loadout, 1000);
            }
            return;
        }

        Document spawnDocument = (Document) document.get("spawn");
        for (StatisticType statisticType : StatisticType.values()) {
            this.spawnStatistics.put(statisticType, spawnDocument.get(statisticType.name(), statisticType == StatisticType.LEVEL ? 1 : 0.0));
        }

        if (document.containsKey("arena")) {
            Document arenaDocument = (Document) document.get("arena");
            for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
                arenaStatistics.put(loadout, arenaDocument.containsKey(loadout.getName().toLowerCase()) ? arenaDocument.getInteger(loadout.getName().toLowerCase(), 1000) : 1000);
            }
            this.globalElo = arenaDocument.getInteger("global", 1000);
        }

        Document kitDocument = (Document) document.get("kit");
        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            if (kitDocument.containsKey(kit.getName().toLowerCase())) {
                Document statistic = (Document) kitDocument.get(kit.getName().toLowerCase());

                this.kitStatistics.put(kit.getName().toLowerCase(), new KitStatistic(statistic));
            }
        }

        Map<String, Document> gameStatistic = (Map<String, Document>) document.get("game");
        for (Map.Entry<String, Document> entry : gameStatistic.entrySet()) {
            String name = entry.getKey();
            Document gameDocument = entry.getValue();
            this.gameStatistics.put(GameType.valueOf(name), new GameStatistic(gameDocument));
        }
    }

    public int get(MatchLoadout loadout) {
        return this.arenaStatistics.getOrDefault(loadout, 1000);
    }

    public void set(MatchLoadout loadout, int newElo) {
        this.arenaStatistics.put(loadout, newElo);
        this.updateElo();
    }

    public void updateElo() {
        int elo = 0;
        int count = 0;
        for (Map.Entry<MatchLoadout, Integer> entry : this.arenaStatistics.entrySet()) {
            MatchLoadout loadout = entry.getKey();
            if (loadout.isRanked()) {
                elo += entry.getValue();
                count++;
            }
        }

        this.globalElo = Math.round(elo / Math.max(1, count));
        this.playerData.markForSave();
    }

    public Document getSpawnData() {
        Document spawnDocument = new Document();
        this.spawnStatistics.forEach(((statisticType, amount) -> spawnDocument.put(statisticType.name(), amount)));
        return spawnDocument;
    }

    public Document getKitData() {
        Document kitDocument = new Document();
        this.kitStatistics.forEach(((kit, stats) -> kitDocument.put(kit.toLowerCase(), stats.toJSON())));
        return kitDocument;
    }

    public Document getArenaData() {
        Document arenaDocument = new Document();
        this.arenaStatistics.forEach(((kit, elo) -> arenaDocument.put(kit.getName().toLowerCase(), elo)));

        arenaDocument.put("global", this.globalElo);
        return arenaDocument;
    }

    public Document getGameData() {
        Document gameDocument = new Document();
        this.gameStatistics.forEach((game, statistic) -> gameDocument.put(game.name(), statistic.toJSON()));
        return gameDocument;
    }

    public Document getData() {
        return new Document("spawn", getSpawnData())
                .append("kit", getKitData())
                .append("game", getGameData())
                .append("arena", getArenaData());
    }

    public KitStatistic get(Kit kit) {
        this.kitStatistics.putIfAbsent(kit.getName().toLowerCase(), new KitStatistic());
        return this.kitStatistics.get(kit.getName().toLowerCase());
    }

    public double get(StatisticType statisticType) {
        if(statisticType == StatisticType.KDR) {
            return this.get(StatisticType.KILLS) / Math.max(this.get(StatisticType.DEATHS), 1);
        }

        this.spawnStatistics.putIfAbsent(statisticType, statisticType.getDefaultValue());
        return this.spawnStatistics.getOrDefault(statisticType, statisticType.getDefaultValue());
    }

    private void updateKDR() {
        spawnStatistics.put(StatisticType.KDR, get(StatisticType.KDR));
        playerData.markForSave();

    }

    public double set(StatisticType statisticType, double newValue) {
        if (statisticType == StatisticType.KILLS || statisticType == StatisticType.DEATHS) {
            updateKDR();
        }
        this.spawnStatistics.putIfAbsent(statisticType, statisticType.getDefaultValue());
        this.spawnStatistics.put(statisticType, newValue);
        this.playerData.markForSave();
        return newValue;
    }

    public double add(StatisticType statisticType, double value) {
        if (statisticType == StatisticType.KILLS || statisticType == StatisticType.DEATHS) {
            updateKDR();
        }

        this.spawnStatistics.putIfAbsent(statisticType, statisticType.getDefaultValue());

        double newValue = this.get(statisticType) + value;
        this.spawnStatistics.put(statisticType, newValue);
        this.playerData.markForSave();
        return newValue;
    }

    public double add(StatisticType statisticType) {
        return this.add(statisticType, 1);
    }

}
