package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityTask;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.HashSet;
import java.util.List;

public class Smite extends Ability implements Listener {
    // Smite nearby players where target is looking at.
    // We should allow smiting 3 times

    public Smite() {
        addProperty("strike-radius", 20., "Radius how far smiting can be applied");
        addProperty("damage-radius", 3., "Radius of damage being striked to players nearby");
        addProperty("damage", 3, "Damage applied for each smite");
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public Material getType() {
        return Material.GOLD_AXE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, false)) return;

        Location location;
        List<Block> blocks = player.getLastTwoTargetBlocks((HashSet<Byte>)null, getProperty("strike-radius").intValue());
        if (blocks.size() > 1 && blocks.get(1).getType() == Material.AIR) {
            Location maxLocation = player.getLocation().add(player.getLocation().getDirection().multiply(getProperty("strike-radius")));
            location = player.getWorld().getHighestBlockAt(maxLocation).getLocation();
        } else {
            location = blocks.get(0).getLocation();
        }

        if (location == null) {
            player.sendMessage(ChatColor.RED + "You can't smite here!");
            return;
        }
        new SmiteTask(player, location).start();
        addCooldown(player);
    }

    private class SmiteTask extends AbilityTask {

        private World world;
        private Location location;

        public SmiteTask(Player player, Location location) {
            super(player, 2250L, 15L);

            this.world = location.getWorld();
            this.location = location;
        }

        @Override
        public void onTick() {
            location.getWorld().strikeLightningEffect(location);
            double damage = getProperty("damage");
            for (Player nearbyPlayer : PlayerUtil.getNearbyPlayers(location, getProperty("damage-radius"))) {
                if (nearbyPlayer == player) continue; // Don't apply to player
                world.strikeLightningEffect(nearbyPlayer.getLocation());
                nearbyPlayer.damage(Protection.isAlly(nearbyPlayer, player) ? damage / 2 : damage, player);
            }
        }

        @Override
        public void onCancel() {

        }
    }
}