package gg.manny.brawl.duelarena.match.queue;

import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import lombok.Getter;

@Getter
public class QueueData {

    private QueueType queueType;

    private MatchLoadout loadout;

    private long startedAt = System.currentTimeMillis();

    private int playerElo;
    private int maxRange, minRange;

    public QueueData(MatchLoadout loadout, QueueType queueType, int playerElo) {
        this.loadout = loadout;
        this.queueType = queueType;

        this.playerElo = playerElo;
        this.maxRange = playerElo + 10;
        this.minRange = playerElo - 10;
    }

    public void incrementRange() {
        if (this.maxRange + 10 >= 3000) {
            this.maxRange = 3000;
        } else {
            this.maxRange = this.maxRange + 10;
        }

        if (this.minRange - 10 <= 0) {
            this.minRange = 0;
        } else {
            this.minRange = this.minRange - 10;
        }
    }
}
