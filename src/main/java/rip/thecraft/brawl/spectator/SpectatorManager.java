package rip.thecraft.brawl.spectator;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectatorManager implements Listener {

    protected final Map<UUID, SpectatorMode> spectators = new HashMap<>();

    public SpectatorManager() {
        Brawl.getInstance().getServer().getPluginManager().registerEvents(new SpectatorListener(this), Brawl.getInstance());
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
