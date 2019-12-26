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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dragon extends Ability implements Listener {

    private final Brawl plugin = Brawl.getInstance();
    private final int MAX_USAGES = 20;

    @Override
    public Material getType() {
        return Material.FIREBALL;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Map<String, String> getProperties(Player player) {
        Map<String, String> usages = new HashMap<>();
        usages.put("usages", player.getMetadata("dragon.usages").get(0).asString());
        return usages;
    }

    @Override
    public void onApply(Player player) {
        player.setMetadata("dragon.usages", new FixedMetadataValue(plugin, 0));
    }

    @Override
    public void onRemove(Player player) {
        player.removeMetadata("dragon.usages", plugin);
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        int usages = player.getMetadata("dragon.usages").get(0).asInt();
        if(usages == MAX_USAGES) {
            this.addCooldown(player, 15000);
            player.setMetadata("dragon.usages", new FixedMetadataValue(plugin, 0));
            return;
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

        player.setMetadata("dragon.usages", new FixedMetadataValue(plugin, usages + 1));
    }
}
