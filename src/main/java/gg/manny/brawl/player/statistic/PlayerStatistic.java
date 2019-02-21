package gg.manny.brawl.player.statistic;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.statistic.KitStatistic;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatistic {

    private Map<StatisticType, Double> statisticMap = new HashMap<>();
    private Map<String, KitStatistic> kitStatisticMap = new HashMap<>();


    public void fromJSON(Document document) {
        Document statisticDocument = (Document) document.get("statistic");
        for(StatisticType statisticType : StatisticType.values()) {
            if(statisticDocument.containsKey(statisticType.name())) {
                this.statisticMap.put(statisticType, document.getDouble(statisticType.name()));
            }
        }

        Document kitStatisticDocument = (Document) document.get("kitStatistic");
        for(Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            if(kitStatisticDocument.containsKey(kit.getName())) {
                Document statistic = (Document) document.get(kit.getName());

                this.kitStatisticMap.putIfAbsent(kit.getName(), new KitStatistic(statistic));
            }
        }

    }

    public Document toJSON() {
        Document statisticDocument = new Document();
        this.statisticMap.forEach(((statisticType, amount) -> statisticDocument.append(statisticType.name(), amount)));

        Document kitStatisticDocument = new Document();
        this.kitStatisticMap.forEach((kitName, kitStatistic) -> kitStatisticDocument.append(kitName, kitStatistic.toJSON()));

        return new Document("statistic", statisticDocument)
                .append("kitStatistic", kitStatisticDocument);
    }

    public KitStatistic get(Kit kit) {
        this.kitStatisticMap.putIfAbsent(kit.getName(), new KitStatistic( ));
        return this.kitStatisticMap.get(kit.getName());
    }

    public double get(StatisticType statisticType) {
        if(statisticType == StatisticType.KDR) {
            return this.get(StatisticType.KILLS) / Math.max(this.get(StatisticType.DEATHS), 1);
        }

        this.statisticMap.putIfAbsent(statisticType, 0.0D);
        return this.statisticMap.getOrDefault(statisticType, 0.0D);
    }

    public double set(StatisticType statisticType, double newValue) {
        this.statisticMap.putIfAbsent(statisticType, 0.0D);
        return this.statisticMap.put(statisticType, newValue);
    }

    public double add(StatisticType statisticType, double value) {
        this.statisticMap.putIfAbsent(statisticType, 0.0D);
        return this.statisticMap.put(statisticType, this.get(statisticType) + value);
    }

    public double add(StatisticType statisticType) {
        return this.add(statisticType, 1);
    }

}
