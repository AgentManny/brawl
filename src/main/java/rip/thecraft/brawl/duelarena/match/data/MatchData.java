package rip.thecraft.brawl.duelarena.match.data;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.ExceptionPlayerNotFound;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.exception.PlayerNotFoundException;

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

    public void addSpectator(UUID spectator) throws PlayerNotFoundException {
        Player player = Bukkit.getPlayer(spectator);
        if (player == null) throw new PlayerNotFoundException("");
    }

}
