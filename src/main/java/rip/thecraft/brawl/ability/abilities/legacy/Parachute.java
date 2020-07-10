package rip.thecraft.brawl.ability.abilities.legacy;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class Parachute extends BukkitRunnable {

    private final Player player;
    private Block[] visual = new Block[11];
    private Block[] visual2 = new Block[11];

    @Override
    public void run() {
        Block block = player.getLocation().getBlock();
        if (block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType() == Material.AIR)  {
            this.visual2 = getVisualDirection();
            this.player.setVelocity(getVector(player));
            this.player.setFallDistance(0.0F);
            this.player.setAllowFlight(true);
            this.player.setFlying(true);
            if (!similarVisual()) {
                removeVisual();
                createVisual();
            }

            if (this.player == null) {
                removeVisual();
                cancel();
            }
        } else {
            cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if (this.player != null) {
            if (this.player.getGameMode() == GameMode.SURVIVAL) {
                this.player.setAllowFlight(false);
            }
            this.player.setFlying(false);
        }

        this.removeVisual();
        super.cancel();
    }

    private boolean similarVisual() {
        return this.visual[0] != null && this.visual[1] == this.visual2[1] && this.visual[6] == this.visual2[6];
    }

    public Block[] getVisualDirection() {
        String direction = this.getDirection(this.player);

        if (direction.equals("W") || direction.equals("E")) {
            return getEastVisual();
        } else if (direction.equals("N") || direction.equals("S")) {
            return getNorthVisual();
        } else if (direction.equals("NW") || direction.equals("SE")) {
            return getNorthWestVisual();
        } else if (direction.equals("SW") || direction.equals("NE")) {
            return getNorthEastVisual();
        }
        return null;
    }

    private Block[] getNorthVisual() {
        Block[] visual = new Block[11];
        visual[0] = this.player.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        visual[1] = visual[0].getRelative(BlockFace.EAST);
        visual[2] = visual[1].getRelative(BlockFace.EAST);
        visual[3] = visual[2].getRelative(BlockFace.DOWN);
        visual[4] = visual[3].getRelative(BlockFace.EAST);
        visual[5] = visual[4].getRelative(BlockFace.DOWN);
        visual[6] = visual[0].getRelative(BlockFace.WEST);
        visual[7] = visual[6].getRelative(BlockFace.WEST);
        visual[8] = visual[7].getRelative(BlockFace.DOWN);
        visual[9] = visual[8].getRelative(BlockFace.WEST);
        visual[10] = visual[9].getRelative(BlockFace.DOWN);
        return visual;
    }

    private Block[] getEastVisual() {
        Block[] visual = new Block[11];
        visual[0] = this.player.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        visual[1] = visual[0].getRelative(BlockFace.NORTH);
        visual[2] = visual[1].getRelative(BlockFace.NORTH);
        visual[3] = visual[2].getRelative(BlockFace.DOWN);
        visual[4] = visual[3].getRelative(BlockFace.NORTH);
        visual[5] = visual[4].getRelative(BlockFace.DOWN);
        visual[6] = visual[0].getRelative(BlockFace.SOUTH);
        visual[7] = visual[6].getRelative(BlockFace.SOUTH);
        visual[8] = visual[7].getRelative(BlockFace.DOWN);
        visual[9] = visual[8].getRelative(BlockFace.SOUTH);
        visual[10] = visual[9].getRelative(BlockFace.DOWN);
        return visual;
    }

    private Block[] getNorthEastVisual() {
        Block[] visual = new Block[11];
        visual[0] = this.player.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        visual[1] = visual[0].getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH);
        visual[2] = visual[1].getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH);
        visual[3] = visual[2].getRelative(BlockFace.DOWN);
        visual[4] = visual[3].getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH);
        visual[5] = visual[4].getRelative(BlockFace.DOWN);
        visual[6] = visual[0].getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH);
        visual[7] = visual[6].getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH);
        visual[8] = visual[7].getRelative(BlockFace.DOWN);
        visual[9] = visual[8].getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH);
        visual[10] = visual[9].getRelative(BlockFace.DOWN);
        return visual;
    }

    private Block[] getNorthWestVisual() {
        Block[] visual = new Block[11];
        visual[0] = this.player.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        visual[1] = visual[0].getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH);
        visual[2] = visual[1].getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH);
        visual[3] = visual[2].getRelative(BlockFace.DOWN);
        visual[4] = visual[3].getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH);
        visual[5] = visual[4].getRelative(BlockFace.DOWN);
        visual[6] = visual[0].getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH);
        visual[7] = visual[6].getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH);
        visual[8] = visual[7].getRelative(BlockFace.DOWN);
        visual[9] = visual[8].getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH);
        visual[10] = visual[9].getRelative(BlockFace.DOWN);
        return visual;
    }

    public void createVisual() {
        this.visual = visual2;
        for (int i = 0; i < this.visual.length; i++) {
            if (this.visual[i].getType() == Material.AIR) {
                this.visual[i].setType(Material.WEB);
            }
        }
    }

    public void removeVisual() {
        for (int i = 0; i < this.visual.length; i++) {
            if (this.visual[i] != null) {
                if (this.visual[i].getType() == Material.WEB) {
                    this.visual[i].setType(Material.AIR);
                }
            }
        }
    }

    private Vector getVector(Player player) {
        double rot = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rot < 0.0D) {
            rot += 360.0D;
        }
        //new Vector(-(this.ForwardSpeed * Math.cos(Math.toRadians(rotation))), this.FastDescend, -(this.ForwardSpeed * Math.sin(Math.toRadians(rotation))));
        return new Vector(-(0.9 /* Forward Speed */ * Math.cos(Math.toRadians(rot))), -0.75 /* Descend Speed */, -(0.9 /* Forward Speed */ * Math.sin(Math.toRadians(rot))));
    }

    private String getDirection(double rot) {
        if (0.0 <= rot && rot < 22.5) {
            return "W";
        }
        if (22.5 <= rot && rot < 67.5) {
            return "NW";
        }
        if (67.5 <= rot && rot < 112.5) {
            return "N";
        }
        if (112.5 <= rot && rot < 157.5) {
            return "NE";
        }
        if (157.5 <= rot && rot < 202.5) {
            return "E";
        }
        if (202.5 <= rot && rot < 247.5) {
            return "SE";
        }
        if (247.5 <= rot && rot < 292.5) {
            return "S";
        }
        if (292.5 <= rot && rot < 337.5) {
            return "SW";
        }
        if (337.5 <= rot && rot < 360.0) {
            return "W";
        }
        return null;
    }

    private String getDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rot < 0.0D) {
            rot += 360.0D;
        }
        return this.getDirection(rot);
    }


}