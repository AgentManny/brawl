package rip.thecraft.brawl.hologram.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;

import java.util.Iterator;

public class HologramListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Iterator<Hologram> iterator = HologramRegistry.getHolograms().iterator();
        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            while (iterator.hasNext()) {
                BaseHologram hologram = (BaseHologram) iterator.next();
                if ((hologram.getViewers() == null || hologram.getViewers().contains(event.getPlayer().getUniqueId())) && hologram.getLocation().getWorld().equals(event.getPlayer().getWorld()) && hologram.getLocation().distanceSquared(event.getPlayer().getLocation()) <= 1600.0) {
                    hologram.show(event.getPlayer());
                }
            }
        }, 20L);
    }

    /* No reason to use Lunar's propriety hologram system
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Client.LUNAR_CLIENT.hasSupport()) {
            LunarClientAPI clientAPI = (LunarClientAPI) Client.LUNAR_CLIENT.getPlugin();
            if (clientAPI.isRunningLunarClient(player)) {
                for (Hologram hologram : HologramRegistry.getHolograms()) {
                    clientAPI.removeHologram(player, hologram.id());
                }
            }
        }
    }
     */

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;
        for (Hologram hologram : HologramRegistry.getHolograms()) {
            BaseHologram hologram2 = (BaseHologram) hologram;
            if ((hologram2.getViewers() == null || hologram2.getViewers().contains(player.getUniqueId())) && hologram2.getLocation().getWorld().equals(player.getWorld())) {
                if (!hologram2.currentWatchers.contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                    hologram2.show(player);
                } else {
                    if (!hologram2.currentWatchers.contains(player.getUniqueId()) || hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                        continue;
                    }
                    hologram2.destroy0(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Hologram hologram : HologramRegistry.getHolograms()) {
            BaseHologram hologram2 = (BaseHologram) hologram;
            if ((hologram2.getViewers() == null || hologram2.getViewers().contains(event.getPlayer().getUniqueId())) && hologram2.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                hologram2.show(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            public void run() {
                for (Hologram hologram : HologramRegistry.getHolograms()) {
                    BaseHologram hologram2 = (BaseHologram) hologram;
                    hologram2.destroy0(event.getPlayer());
                    if ((hologram2.getViewers() == null || hologram2.getViewers().contains(event.getPlayer().getUniqueId())) && hologram2.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                        hologram2.show(event.getPlayer());
                    }
                }
            }
        }.runTaskLater(Brawl.getInstance(), 10L);
    }
}