package rip.thecraft.brawl.game.games;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.util.BukkitUtil;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WoolShuffle extends Game implements Listener {

    private static final long MINIMUM_TIME = 2500L;
    private static final long REDUCE_TIME = 750L;
    private static final long DEFAULT_TIME = TimeUnit.SECONDS.toMillis(10);
    private long currentTime;

    private World world;

    Location posOne, posTwo;
    private Cuboid cuboid;

    private DyeColor chosenColor;

    private final int defaultTime = 15;
    private int timer;

    private int round;
    private BukkitTask task;

    public WoolShuffle() {
        super(GameType.WOOL_SHUFFLE, GameFlag.WATER_ELIMINATE, GameFlag.DOUBLE_JUMP, GameFlag.NO_FALL, GameFlag.NO_PVP);
    }

    @Override
    public void setup() {
        this.currentTime = System.currentTimeMillis() + DEFAULT_TIME;
        this.round = 0;
        chosenColor = getRandomColor();
        posOne = getLocationByName("Pos1");
        posTwo = getLocationByName("Pos2");
        this.cuboid = new Cuboid(posOne, posTwo);

        setArea(DyeColor.WHITE);

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getLocationByName("Lobby"));
        });

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::startRound, 15L);
    }

    @Override
    public void clear() {
        setArea(DyeColor.WHITE);
    }

    @Override
    public void end() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        super.end();
    }

    public void startRound() {
        if (this.state == GameState.ENDED || this.state == GameState.FINISHED) return;
        state = GameState.GRACE_PERIOD;
        round++;

        if (task != null) {
            task.cancel();
            task = null;
        }

        setTime(3);
        task = new BukkitRunnable() {
            public void run() {
                if (getTime() == 0) {
                    start();
                    // Add people their wool
                    setTime(-1);
                    cancel();
                    return;
                }

                switch (getTime()) {
                    case 3:
                    case 2:
                    case 1:
                        if (round == 1) {
                            setArea(null); // for cool effects
                        }
                        playSound(Sound.NOTE_PIANO, 1L, 1L);
                        broadcast(Game.PREFIX + ChatColor.WHITE + (round == 1 ? "First Round" : "Round #" + round) + ChatColor.YELLOW + " will start in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(getTime()) + ChatColor.YELLOW + ".");
                        break;
                    default:
                        break;
                }

                setTime(getTime() - 1);
            }
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);

    }

    @Override
    public void start() {
        state = GameState.STARTED;
        this.currentTime = System.currentTimeMillis() + getMaxTime();
        chosenColor = getRandomColor();

        regenerate();
        this.task = new BukkitRunnable() {

            public void run() {
                if (currentTime < System.currentTimeMillis()) {
                    cancel();
                    remove();
                    return;
                }

                int millisLeft = (int) (currentTime - System.currentTimeMillis());
                float percentLeft = (float) millisLeft / getMaxTime();

                getAlivePlayers().forEach(gamePlayer -> {
                    Player player = gamePlayer.toPlayer();
                    player.setExp(percentLeft);
                    player.setLevel(millisLeft / 1_000);
                });
            }

        }.runTaskTimer(Brawl.getInstance(), 1L, 1L);
    }

    public long getMaxTime() {
        return Math.max(MINIMUM_TIME, DEFAULT_TIME - (round <= 1 ? 0 : REDUCE_TIME * round));
    }

    private Multimap<DyeColor, Location> blockData = ArrayListMultimap.create(); // Allows to remove specific colours
    public void regenerate() {
        blockData.clear(); // Reset

        broadcast(Game.PREFIX + ChatColor.YELLOW + "Color chosen is now " + BukkitUtil.getFriendlyName(chosenColor) + ChatColor.YELLOW + ".");
        broadcast(Game.PREFIX + ChatColor.YELLOW + "Color chosen is now " + BukkitUtil.getFriendlyName(chosenColor) + ChatColor.YELLOW + ".");
        broadcast(Game.PREFIX + ChatColor.YELLOW + "Color chosen is now " + BukkitUtil.getFriendlyName(chosenColor) + ChatColor.YELLOW + ".");

        state = GameState.STARTED;
        ItemStack item = getItem();
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, item);
            }
            player.updateInventory();
        });

        while (!blockData.containsKey(chosenColor)) {
            setArea(null);
        }
    }

    public void setArea(DyeColor preferedColor) {
        int y = cuboid.getLowerY(); // Should only be 1 layer so won't matter
        for (int x = cuboid.getLowerX(); x < cuboid.getUpperX(); x+=3){
            for (int z = cuboid.getLowerZ(); z < cuboid.getUpperZ(); z+=3){

                int newX = x + 3;
                int newZ = z + 3;
                DyeColor color = preferedColor == null ? getRandomColor() : preferedColor;
                for (int boopX = x; boopX < newX; boopX++) {
                    for (int boopZ = z; boopZ < newZ; boopZ++) {
                        Block block = Bukkit.getWorld("world").getBlockAt(boopX, y, boopZ);
                        block.setType(Material.WOOL);

                        BlockState state = block.getState();
                        state.setData(new Wool(color));
                        state.update();

                        blockData.put(color, block.getLocation());
                    }
                }
            }
        }
    }

    public void remove() {
        for (Map.Entry<DyeColor, Location> entry : blockData.entries()) {
            DyeColor color = entry.getKey();
            Location location = entry.getValue();
            if (color != chosenColor) {
                location.getBlock().setType(Material.AIR); // Poop
            }
        }

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, null);
            }
            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 1);
            player.updateInventory();
        });

        state = GameState.GRACE_PERIOD;
        startRound();
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.WHITE + "Game: " + ChatColor.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.WHITE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add("    ");
        if (state != GameState.FINISHED) {
            long millisLeft = currentTime - System.currentTimeMillis();
            toReturn.add(CC.WHITE + "Round: " + CC.LIGHT_PURPLE + this.round + (state == GameState.STARTED && millisLeft > 0 ? CC.GRAY + " (" + DurationFormatter.getTrailing(millisLeft) + "s)" : ""));
            toReturn.add(CC.WHITE + "Round Speed: " + CC.LIGHT_PURPLE + DurationFormatter.getRemaining(getMaxTime()));
            toReturn.add(CC.BLUE + "   ");
        }
        if (this.state == GameState.STARTED) {
            toReturn.add(CC.WHITE + "Color: "  + (chosenColor == null ? "None" : BukkitUtil.getFriendlyName(chosenColor)));
        } else if (this.state == GameState.FINISHED) {
            toReturn.add(CC.WHITE + "Final Round: " + CC.LIGHT_PURPLE + this.round);
            toReturn.add(CC.BLUE + "   ");
            boolean winners = this.winners.size() > 1;
            if (winners) {
                toReturn.add(CC.WHITE + "Winners: ");
                for (GamePlayer winner : this.winners) {
                    toReturn.add(CC.LIGHT_PURPLE + "  " + winner.getName());
                }
            } else if (!this.winners.isEmpty()) {
                toReturn.add(CC.WHITE + "Winner: " + CC.LIGHT_PURPLE + this.winners.get(0).getName());
            } else {
                toReturn.add(CC.WHITE + "Winner: " + CC.RED + "None");
            }
        } else {
            toReturn.add(CC.LIGHT_PURPLE + "Waiting...");
        }
        return toReturn;
    }

    private DyeColor getRandomColor() {
        return DyeColor.values()[Brawl.RANDOM.nextInt(DyeColor.values().length)];
    }

    private ItemStack getItem() {
        return new ItemBuilder(Material.CARPET)
                .data(this.chosenColor.getData())
                .name(BukkitUtil.getFriendlyName(this.chosenColor))
                .build();
    }

    @EventHandler
    public void onPlayerDamage(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof WoolShuffle) {
                GamePlayer gamePlayer = this.getGamePlayer(player);
                if (gamePlayer != null && gamePlayer.isAlive()) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        }
    }

}
