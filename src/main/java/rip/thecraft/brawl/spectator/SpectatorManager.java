package rip.thecraft.brawl.spectator;

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

    public SpectatorMode addSpectator(Player player) {
        SpectatorMode spectatorMode = SpectatorMode.init(player, null);
        spectators.put(player.getUniqueId(), spectatorMode);
        return spectatorMode;
    }

    public void removeSpectator(Player player) {
        SpectatorMode spectator = getSpectator(player);
        if (spectator != null) {
            spectator.leave();
        }
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
