package gg.manny.brawl.player.cps;

import gg.manny.brawl.Brawl;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ClickTracker implements Listener  {

    private final Brawl plugin;

    private static Map<UUID, Integer> cpsCount = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            cpsCount.putIfAbsent(uuid, 0);

            cpsCount.put(uuid, cpsCount.get(uuid) + 1);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (cpsCount.containsKey(uuid)) {
                    cpsCount.put(uuid, Math.max(0, cpsCount.get(uuid) - 1));
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cpsCount.remove(event.getPlayer().getUniqueId());
    }

    public static int getCPS(Player player) {
        cpsCount.putIfAbsent(player.getUniqueId(), 0);
        return cpsCount.get(player.getUniqueId());
    }



}
