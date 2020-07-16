package rip.thecraft.brawl.duelarena.match.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class MatchData {

    private Map<UUID, Integer> wins = new HashMap<>();

    private Map<UUID, PostMatchData> inventories = new HashMap<>();

    private Map<UUID, Integer> totalHits = new HashMap<>();
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

}
