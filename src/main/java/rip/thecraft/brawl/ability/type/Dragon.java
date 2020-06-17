package rip.thecraft.brawl.ability.type;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;

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

                    ParticleEffect.FLAME.send(blockLoc, 0.1f, 1);
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
