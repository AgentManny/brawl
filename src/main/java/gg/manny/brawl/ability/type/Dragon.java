package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.brawl.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dragon extends Ability implements Listener {

    private final Brawl plugin = Brawl.getInstance();

    @Override
    public Material getType() {
        return Material.FIREBALL;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player, 15000);
        new BukkitRunnable() {

            @Override
            public void run() {
                int times;
                if(player.hasMetadata("dragon.timer")) {
                    times = player.getMetadata("dragon.timer").get(0).asInt();
                }
                else {
                    times = 4;
                    player.setMetadata("dragon.timer", new FixedMetadataValue(plugin, times));
                }
                List<Block> blocks = player.getLineOfSight(null, 10);
                List<Player> nearbyPlayers = null;
                for(Block block : blocks) {
                    Location blockLoc = block.getLocation();
                    if(nearbyPlayers == null) {
                        nearbyPlayers = BrawlUtil.getNearbyPlayers(player, 10);
                    }

                    ParticleEffect.FLAME.send(blockLoc, 0.1f, 1);
                    for(Player target : nearbyPlayers) {
                        Location targetLoc = target.getLocation();
                        if(targetLoc.getBlockX() == blockLoc.getBlockX()
                                && targetLoc.getBlockZ() == blockLoc.getBlockZ())
                            target.setFireTicks(60);
                    }
                }
                player.setMetadata("dragon.timer", new FixedMetadataValue(plugin, times - 1));
                if(player.getMetadata("dragon.timer").get(0).asInt() == 0) {
                    player.removeMetadata("dragon.timer", plugin);
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 1L, 20L);
    }
}
