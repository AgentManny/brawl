package rip.thecraft.brawl.spectator;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.util.VisibilityUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpectatorManager implements Listener {

    protected final Map<UUID, SpectatorMode> spectators = new ConcurrentHashMap<>();

    public SpectatorManager() {
        Brawl.getInstance().getServer().getPluginManager().registerEvents(new SpectatorListener(this), Brawl.getInstance());
    }


    /**
     * Remove all spectators from a game.
     * @param Object The data to check.
     */
    public void removeSpectators(SpectatorMode.SpectatorType type, Object data) {
        for (Map.Entry<UUID, SpectatorMode> entry : spectators.entrySet()) {
            SpectatorMode mode = entry.getValue();
            if (mode.getSpectating() == type) {
                mode.leave();
            }
        }
    }


    public SpectatorMode addSpectator(Player spectator) {
        return addSpectator(spectator, null, null);
    }

    public SpectatorMode addSpectator(Player spectator, Location location) {
        return addSpectator(spectator, null, location);
    }

    public SpectatorMode addSpectator(Player spectator, Player target) {
        return addSpectator(spectator, target, null);
    }

    public SpectatorMode addSpectator(Player spectator, Player target, Location location) {
        SpectatorMode spectatorMode = SpectatorMode.init(spectator, target, location);
        spectators.put(spectator.getUniqueId(), spectatorMode);

        DuelArenaHandler matchHandler = Brawl.getInstance().getMatchHandler();
        matchHandler.cleanup(spectator.getUniqueId());
        matchHandler.refreshQuickqueue();

        VisibilityUtils.updateVisibility(spectator);
        return spectatorMode;
    }

    public void removeSpectator(UUID uuid) {
        SpectatorMode spectator = spectators.get(uuid);
        if (spectator != null) {
            spectator.leave();
        }
    }
    public void removeSpectator(Player player) {
        removeSpectator(player.getUniqueId());
    }

    public SpectatorMode getSpectator(Player player) {
        return spectators.get(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectators.containsKey(player.getUniqueId());
    }

    public Collection<SpectatorMode> getSpectators() {
        return spectators.values();
    }
}
