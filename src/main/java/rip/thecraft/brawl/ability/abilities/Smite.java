package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.task.AbilityTask;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.HashSet;
import java.util.List;

@AbilityData(
        name = "Smite",
        description = "Summon bolts of lightning to damage your enemies.",
        icon = Material.GOLD_AXE,
        color = ChatColor.WHITE
)
public class Smite extends Ability implements Listener {
    // Smite nearby players where target is looking at.
    // We should allow smiting 3 times

    @AbilityProperty(id = "strike-radius", description = "Radius how far smiting can be applied")
    public int strikeRadius = 20;

    @AbilityProperty(id = "damage-radius", description = "Radius of damage being striked to players nearby")
    public int damageRadius = 3;

    @AbilityProperty(id = "damage", description = "Damage applied for each smite")
    public int damage = 4;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;

        Location location;
        List<Block> blocks = player.getLastTwoTargetBlocks((HashSet<Byte>) null, strikeRadius);
        if (blocks.size() > 1 && blocks.get(1).getType() == Material.AIR) {
            Location maxLocation = player.getLocation().add(player.getLocation().getDirection().multiply(strikeRadius));
            location = player.getWorld().getHighestBlockAt(maxLocation).getLocation();
        } else {
            location = blocks.get(0).getLocation();
        }

        if (location == null) {
            player.sendMessage(ChatColor.RED + "You can't smite here!");
            return;
        }
        new SmiteTask(this, player, location).start();
        addCooldown(player);
    }

    private class SmiteTask extends AbilityTask {

        private World world;
        private Location location;

        public SmiteTask(Ability ability, Player player, Location location) {
            super(ability, player, 2250L, 15L);

            this.world = location.getWorld();
            this.location = location;
        }

        @Override
        public void onTick() {
            location.getWorld().strikeLightningEffect(location);
            for (Player nearbyPlayer : PlayerUtil.getNearbyPlayers(location, damageRadius)) {
                if (nearbyPlayer == player) continue; // Don't apply to player
                world.strikeLightningEffect(nearbyPlayer.getLocation());
                nearbyPlayer.damage(Protection.isAlly(nearbyPlayer, player) ? damage / 2. : damage, player);
            }
        }

        @Override
        public void onCancel() {

        }
    }
}