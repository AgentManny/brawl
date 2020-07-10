package rip.thecraft.brawl.ability.abilities.legacy;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.SchedulerUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IceSpikes extends Ability {

    private static final BlockFace[] AXIAL = new BlockFace[] {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH
    };

    private static final BlockFace[] RADIAL = new BlockFace[] {
            BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST
    };

    private int radius = 5;
    private int time = 15;
    private Material spikes = Material.PACKED_ICE;

    @Override
    public String getName() {
        return "Ice Spikes";
    }

    @Override
    public Material getType() {
        return Material.ICE;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to activate this ability.");
            return;
        }

        this.addCooldown(player);
        Block loc = player.getTargetBlock(new HashSet<Material>(), 9);

        int iterations = 0;
        while (iterations < 11 && !loc.getType().isSolid()) {
            iterations++;
            loc = loc.getRelative(BlockFace.DOWN);
        }

        for (int i = 0; i < 5; i++) {
            Location spawnAt = loc.getLocation().clone().add(Brawl.RANDOM.nextInt(10) - 5, Brawl.RANDOM.nextInt(10) - 5, Brawl.RANDOM.nextInt(10) - 5);
            int chosen = 15 + Brawl.RANDOM.nextInt(7);
            this.createPillar(spawnAt, chosen);
        }
    }

    private void damageNearby(Location location) {
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
        for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++) {
            for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();
                for (Entity e : new Location(location.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
                    if (e.getLocation().distance(location) <= radius && e.getLocation().getBlock() != location.getBlock() && !RegionType.SAFEZONE.appliesTo(e.getLocation())) {
                        if (e instanceof Player) {
                            Player p = (Player) e;
                            p.damage(4.5D);
                        }
                    }
                }
            }
        }
    }

    private void createPillar(Location location, int height) {
        final Location cloned = location.clone();
        final List<Block> changed = new ArrayList<>();

        for (int i = 0; i < height; i ++) {
            Block b = location.add(0, 1, 0).getBlock();
            if (!RegionType.SAFEZONE.appliesTo(b.getLocation())) {
                if (b.getType() == Material.AIR) {
                    b.setType(this.spikes);
                    b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                    changed.add(b);
                    damageNearby(b.getLocation());
                }
            }
        }

        for (BlockFace rad : AXIAL) {
            Location base = cloned.getBlock().getRelative(rad).getLocation();
            for (int i = 0; i < height - 3; i ++) {
                Block b = base.add(0, 1, 0).getBlock();
                if (!RegionType.SAFEZONE.appliesTo(b.getLocation())) {
                    if (b.getType() == Material.AIR) {
                        b.setType(this.spikes);
                        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                        changed.add(b);
                        damageNearby(b.getLocation());
                    }
                }
            }
        }
        for (BlockFace rad : RADIAL) {
            Location base = cloned.getBlock().getRelative(rad).getLocation();
            for (int i = 0; i < height - 6; i ++) {
                Block b = base.add(0, 1, 0).getBlock();
                if (!RegionType.SAFEZONE.appliesTo(b.getLocation())) {
                    if (b.getType() == Material.AIR) {
                        changed.add(b);
                        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                        b.setType(this.spikes);
                        damageNearby(b.getLocation());
                    }
                }
            }
        }
        SchedulerUtil.runTaskLater(() -> changed.forEach(block -> block.setType(Material.AIR)), time * 20, false);
    }
}
