package rip.thecraft.brawl.game.statistic;

import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Data
public class GameStatistic {

    private int played, wins, losses;
    private transient Map<String, Object> properties = new HashMap<>();

    public GameStatistic(Document document) {
        this.played = document.getInteger("played");
        this.wins = document.getInteger("wins");
        this.losses = document.getInteger("losses");

        for (Map.Entry<String, Object> entry : ((Document) document.get("properties")).entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }
    }

    public Document toJSON() {
        return new Document("played", this.played)
                .append("wins", this.wins)
                .append("losses", this.losses)
                .append("properties", new Document(properties));
    }

    public double addPlayed(double value) {
        return this.played += value;
    }

    public double addWins(double value) {
        return this.wins += value;
    }

    public double addLosses(double value) {
        return this.losses += value;
    }

    public double addPlayed() {
        return this.played += 1;
    }

    public double addWins() {
        return this.wins += 1;
    }

    public double addLosses() {
        return this.losses += 1;
    }

}
