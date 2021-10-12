package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.ParticleEffect;

import java.util.HashSet;
import java.util.List;

@AbilityData(icon = Material.BLAZE_POWDER, color = ChatColor.GOLD)
public class Blaze extends Ability implements Listener {

    private final Brawl plugin = Brawl.getInstance();

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);
        new BukkitRunnable() {

            private int times = 0;

            @Override
            public void run() {
                List<Block> blocks = player.getLineOfSight(new HashSet<Material>(), 10);
                List<Player> nearbyPlayers = null;
                for(Block block : blocks) {
                    Location blockLoc = block.getLocation();
                    if(nearbyPlayers == null) {
                        nearbyPlayers = BrawlUtil.getNearbyPlayers(player, 10);
                    }

                    ParticleEffect.FLAME.display(0, 0, 0, 1.5f, 2, blockLoc, EFFECT_DISTANCE);

                    for(Player target : nearbyPlayers) {
                        Location targetLoc = target.getLocation();
                        if(targetLoc.getBlockX() == blockLoc.getBlockX()
                                && targetLoc.getBlockZ() == blockLoc.getBlockZ())
                            target.setFireTicks(60);
                    }
                }
                times++;
                if(times == 5)
                    cancel();
            }

        }.runTaskTimer(plugin, 1L, 5L);
    }
}
