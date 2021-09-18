package rip.thecraft.brawl.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.event.KitDeactivateEvent;

import java.util.Collection;
import java.util.UUID;

public class EffectRestorer implements Listener {

    private final Table<UUID, PotionEffectType, PotionEffect> restores;

    public EffectRestorer(Brawl plugin) {
        this.restores = HashBasedTable.create();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitDeactivate(KitDeactivateEvent event) {
        this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.restores.rowKeySet().remove(event.getEntity().getUniqueId());
    }


    public void setRestoreEffect(Player player, PotionEffect effect) {
        if (effect == null) {
            return;
        }
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (active.getType().getId() == effect.getType().getId()) {
                if (effect.getAmplifier() < active.getAmplifier()) {
                    return;
                }
                if ((effect.getAmplifier() == active.getAmplifier()) && (effect.getDuration() < active.getDuration())) {
                    return;
                }
                this.restores.put(player.getUniqueId(), active.getType(), active);
                shouldCancel = false;
            }
        }
        player.addPotionEffect(effect, true);
        if ((shouldCancel) && (effect.getDuration() > 100)) {
            this.restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if ((livingEntity instanceof Player)) {
            Player player = (Player) livingEntity;
            PotionEffect previous = this.restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                event.setCancelled(true);
                new BukkitRunnable(){
                    public void run() {
                        player.addPotionEffect(previous, true);
                    }
                }.runTask(Brawl.getInstance());
            }
        }
    }
}