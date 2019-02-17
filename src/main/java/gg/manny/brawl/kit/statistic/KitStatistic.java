package gg.manny.brawl.kit.statistic;

import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Data
public class KitStatistic {

    private int uses, kills, deaths;
    private transient Map<String, Object> properties = new HashMap<>();

    public KitStatistic(Document document) {
        this.uses = document.getInteger("uses");
        this.kills = document.getInteger("kills");
        this.deaths = document.getInteger("deaths");

        for (Map.Entry<String, Object> entry : ((Document) document.get("properties")).entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }
    }

    public Document toJSON() {
        return new Document("uses", this.uses)
                .append("kills", this.kills)
                .append("deaths", this.deaths)
                .append("properties", new Document(properties));
    }

    public double addKills(double value) {
        return this.kills += value;
    }

    public double addDeaths(double value) {
        return this.deaths += value;
    }

    public double addUses(double value) {
        return this.uses += value;
    }

    public double addKills() {
        return this.kills += 1;
    }

    public double addDeaths() {
        return this.deaths += 1;
    }

    public double addUses() {
        return this.uses += 1;
    }

}
