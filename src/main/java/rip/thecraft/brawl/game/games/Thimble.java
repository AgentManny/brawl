package rip.thecraft.brawl.game.games;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.game.type.RoundGame;
import gg.manny.streamline.util.cuboid.Cuboid;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Thimble extends RoundGame {
    {
        setTimeLimitEliminate(true);
        setExpShowTimer(false);
        setMaxRoundTime(TimeUnit.SECONDS.toMillis(12));
    }

    Location platformPosOne, platformPosTwo, jumpPosOne, jumpPosTwo;
    private Cuboid platformCuboid, jumpCuboid;

    private Map<Location, BlockState> jumpBlocks = new HashMap<>();

    private boolean platformRemoved = false;
    private int removeJumpPlatform = 5;

    private int percentageWater;

//    private int roundShrink = 3; // When should the platform shrink
//    private boolean frenzy = false; // Should Jump platform be removed

    public Thimble() {
        super(GameType.THIMBLE, GameFlag.FALL_ELIMINATE, GameFlag.NO_FALL, GameFlag.NO_PVP);
    }

    @Override
    public void setup() {
        // Map update
        platformPosOne = getLocationByName("Platform1");
        platformPosTwo = getLocationByName("Platform2");
        platformCuboid = new Cuboid(platformPosOne, platformPosTwo);
        setPlatform(100);

        jumpBlocks.clear();

        jumpPosOne = getLocationByName("JumpPlatform1");
        jumpPosTwo = getLocationByName("JumpPlatform2");
        jumpCuboid = new Cuboid(jumpPosOne, jumpPosTwo);

        jumpCuboid.forEach(location -> {
            Block block = location.getBlock();
            if (block.getType() != Material.AIR) {
                jumpBlocks.put(location, block.getState());
            }
        });

        percentageWater = 75;
        removeJumpPlatform = 5;

        super.setup();
    }

    @Override
    public void clear() {
        platformCuboid = new Cuboid(platformPosOne, platformPosTwo);
        setPlatform(100);
        setJumpPlatform(true);
    }

    @Override
    public void tick() {
        if (state == GameState.STARTED && !platformRemoved) {
            if (removeJumpPlatform == 0) {
                setJumpPlatform(false);
            } else {
                removeJumpPlatform--;
            }
        }
        super.tick();
    }

    private void setPlatform(int percentageWater) {
        int y = platformCuboid.getLowerY(); // Should only be 1 layer so won't matter
        for (int x = platformCuboid.getLowerX(); x <= platformCuboid.getUpperX(); x++) {
            for (int z = platformCuboid.getLowerZ(); z <= platformCuboid.getUpperZ(); z++) {
                Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                block.setType(Math.random() * 100 <= percentageWater ? Material.WATER : Material.GOLD_BLOCK);
            }
        }
    }

    private void setJumpPlatform(boolean revert) {
        // Store original
        if (revert) {
            for (Location location : jumpCuboid) {
                if (jumpBlocks.containsKey(location)) {
                    BlockState blockState = jumpBlocks.get(location);
                    Block block = location.getBlock();
                    block.setType(blockState.getMaterial());
                    block.setData(blockState.getData().getData());
                }
            }
            platformRemoved = false;
        } else {
            platformRemoved = true;
            for (Location location : jumpCuboid) {
                jumpCuboid.getWorld().fastBlockChange(location.toVector(), new MaterialData(Material.AIR));
            }
        }
    }

    @Override
    public void onRoundSetup() {
        if (round != 0) {
            int minWater = round >= 20 ? 5 : 20;
            percentageWater = Math.max(minWater, percentageWater - 5);
        }
//        if (round <= roundShrink + 5) {
//            if (round == roundShrink) {
//                broadcast(Game.PREFIX + ChatColor.YELLOW + "Platform will begin to shrink...");
//            }
//            for (Cuboid wall : platformCuboid.getWalls()) {
//                wall.forEach(location -> location.getBlock().setType(Material.BEDROCK));
//            }
//            this.platformCuboid = platformCuboid.outset(CuboidDirection.HORIZONTAL, -3);
//        }
        setPlatform(percentageWater);
    }

    @Override
    public void onRoundStart() {
        removeJumpPlatform = 5;
        teleport(getLocationByName("Jump"));
    }

    @Override
    public void onRoundEnd() {
        setJumpPlatform(true);
    }

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (elimination == GameElimination.FALL) {
            if (played.contains(player.getUniqueId())) return;
            playSound(location, Sound.FIREWORK_TWINKLE2, 1, 1);
        }
        super.handleElimination(player, location, elimination);
    }

    public void processMovement(Player player, GamePlayer gamePlayer, Location from, Location to) {
        if (state == GameState.STARTED) {
            Block block = to.getBlock();
            if (block.isLiquid()) {
                to.getBlock().setType(Material.GOLD_BLOCK);
                player.playSound(player.getLocation(), Sound.SPLASH, 1, 1.2F);
                to.getWorld().spawn(to, Firework.class);
                player.setFallDistance(0);
                player.teleport(getLocationByName("Lobby"));
                played.add(player.getUniqueId());
                if (isRoundOver()) {
                    nextRound();
                }
            }
        }
    }
}
