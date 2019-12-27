package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FluffyHandcuffs extends Ability {

    @Override
    public Material getType() {
        return Material.TRIPWIRE_HOOK;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }


    @Override
    public String getName() {
        return "Fluffy Handcuffs";
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to activate this ability.");
            return;
        }


        this.addCooldown(player);

        generateSphere(player.getLocation(), 5);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
    }

    /**
     * @param centerBlock Define the center of the sphere
     * @param radius      Radius of your sphere
     * @return Returns the locations of the blocks in the sphere
     */
    private void generateSphere(Location centerBlock, int radius) {

        List<Block> circleBlocks = new ArrayList<>();


        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        int lowestY = centerBlock.getBlock().getRelative(BlockFace.DOWN).getLocation().getBlockY();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                if (y >= lowestY) {
                    for (int z = bz - radius; z <= bz + radius; z++) {

                        double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));

                        if (distance < radius * radius && !(distance < ((radius - 1) * (radius - 1)))) {

                            Location l = new Location(centerBlock.getWorld(), x, y, z);
                            Block block = l.getBlock();
                            if (block.getType() == Material.AIR) {
                                circleBlocks.add(block);
                            }
                        }

                    }
                }
            }
        }

        /*
        int height = 1;
        Block center = centerBlock.getBlock().getRelative(BlockFace.DOWN);
        for (int currentheight = 0; currentheight < height; currentheight++) { //loop through all the y values(height)
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    Block block = center.getRelative(x, currentheight, z);
                    if (!circleBlocks.contains(block)) {
                        circleBlocks.add(block);
                    }
                }
            }
        }
        */

        Location location = centerBlock.getBlock().getRelative(BlockFace.DOWN).getLocation();
        int cx = location.getBlockX();
        int cz = location.getBlockZ();

        int rSquared = radius * radius;

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    Block block = centerBlock.getWorld().getBlockAt(x, by - 1, z);
                    if (block.getType() == Material.AIR) {
                        if (!circleBlocks.contains(block)) {
                            circleBlocks.add(block);
                        }
                    }
                }
            }
        }

        for (Block state : circleBlocks) {
            if (state.getType() == Material.AIR) {

                state.setType(Material.ICE);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Block state : circleBlocks) {
                    if (state.getType() == Material.ICE) {
                        state.setType(Material.AIR);
                    }
                }

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                for (Block state : circleBlocks) {
                    if (state.getType() == Material.ICE) {
                        state.setType(Material.AIR);
                    }
                }
                super.cancel();
            }
        }.runTaskLater(Brawl.getInstance(), 20L * 5);

    }

}
