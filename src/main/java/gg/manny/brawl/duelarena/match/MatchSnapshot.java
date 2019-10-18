package gg.manny.brawl.duelarena.match;

import gg.manny.brawl.duelarena.match.data.PostMatchData;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@RequiredArgsConstructor
public class MatchSnapshot {

    public static final int DURATION = (int) TimeUnit.MINUTES.toSeconds(5);

    private final String id; // Match id

    private long timeToLive = System.currentTimeMillis();

    private Map<UUID, PostMatchData> inventories = new HashMap<>();

    public int getLifetime() {
        return ((int) (System.currentTimeMillis() - timeToLive) / 1000);
    }

    public boolean isValid() {
        return (getLifetime() <= DURATION);
    }

}
