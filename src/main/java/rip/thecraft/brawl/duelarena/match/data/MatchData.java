package rip.thecraft.brawl.duelarena.match.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class MatchData {

    private static final long LAST_HIT_MILLIS = 1000L; // We use this to track combos

    private Map<UUID, Integer> wins = new HashMap<>();

    private Map<UUID, PostMatchData> inventories = new HashMap<>();

    @Setter private UUID lastHit = null;
    @Setter private long lastHitTime = -1L;

    private Map<UUID, Integer> totalHits = new HashMap<>();

    private Map<UUID, Integer> combos = new HashMap<>();
    private Map<UUID, Integer> longestCombo = new HashMap<>();

    private Set<UUID> spectators = new HashSet<>();

    public String getFriendlySpectators() {
        return spectators.stream().map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(HumanEntity::getName)
                .collect(Collectors.joining(", "));
    }

    public void addHits(Player player) {
        totalHits.putIfAbsent(player.getUniqueId(), 0);
        totalHits.put(player.getUniqueId(), totalHits.get(player.getUniqueId()) + 1);
    }

    public void setLastHit(UUID lastHit) {
        if (this.lastHit == lastHit) {
            if (lastHitTime >= System.currentTimeMillis()) {
                combos.put(lastHit, combos.getOrDefault(lastHit, 0) + 1);
            } else {
                longestCombo.put(lastHit, Math.max(combos.getOrDefault(lastHit, 0), longestCombo.getOrDefault(lastHit, 1)));
                combos.put(lastHit, 0);
            }
        } else {
            combos.clear();
        }
        this.lastHit = lastHit;
        this.lastHitTime = System.currentTimeMillis() + LAST_HIT_MILLIS;
    }

    public int getCombos(UUID uuid) {
        combos.putIfAbsent(lastHit, 0);
        return combos.get(lastHit);
    }

    public int getLongestCombo(UUID uuid) {
        longestCombo.putIfAbsent(lastHit, 0);
        return longestCombo.get(lastHit);
    }
}
