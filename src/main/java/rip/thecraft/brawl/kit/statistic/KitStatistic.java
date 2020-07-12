package rip.thecraft.brawl.kit.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class KitStatistic {

    private int uses = 0;
    private int kills = 0;
    private int deaths = 0;

    private transient Map<String, Object> properties = new HashMap<>();

    public KitStatistic(Document document) {
        if (document == null) return;
        this.uses = document.getInteger("uses", 0);
        this.kills = document.getInteger("kills", 0);
        this.deaths = document.getInteger("deaths", 0);

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
