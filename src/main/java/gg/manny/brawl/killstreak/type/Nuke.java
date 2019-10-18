package gg.manny.brawl.killstreak.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.region.RegionType;
import gg.manny.brawl.util.BrawlUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.stream.Collectors;

public class Nuke extends Killstreak implements Listener {

    @Override
    public int[] getKills() {
        return new int[] { 50 };
    }

    @Override
    public String getName() {
        return "Warhead Detonator";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public Material getType() {
        return Material.SKULL_ITEM;
    }

    @Override
    public void onActivate(Player player, PlayerData playerData) {

        List<Player> affectedPlayers =  BrawlUtil.getNearbyPlayers(player, 50)
                .stream().filter(rip -> !RegionType.SAFEZONE.appliesTo(rip.getLocation()))
                .collect(Collectors.toList());

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + player.getName() + " has deployed Warhead Strike!");
        Bukkit.broadcastMessage(" ");

        for (Player deadmen : affectedPlayers) {
            if (deadmen == player) continue;
            final Location loc = deadmen.getLocation();
            final World world = loc.getWorld();

            for (int i = 0; i < 1; i++) {
                for (int x = -10; x <= 10; x += 5) {
                    for (int z = -10; z <= 10; z += 5) {
                        TNTPrimed tnt = world.spawn(new Location(world, loc.getBlockX() + x, world.getHighestBlockYAt(loc) + 64, loc.getBlockZ() + z), TNTPrimed.class);
                        tnt.setMetadata("warhead", new FixedMetadataValue(Brawl.getInstance(), player.getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) event.getDamager();
                if (tnt.hasMetadata("warhead")) {
                    Player detonator = Bukkit.getPlayer(tnt.getMetadata("warhead").get(0).asString());
                    Player victim = (Player) event.getEntity();
                    if (detonator != null) {
                        if (detonator == event.getEntity()) {
                            event.setCancelled(true);
                        } else {
                            event.setDamage(event.getDamage() * 2);
                            victim.damage(0, detonator);
                        }
                    }
                }

            }
        }
    }
}
