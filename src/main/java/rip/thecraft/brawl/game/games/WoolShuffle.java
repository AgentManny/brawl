package rip.thecraft.brawl.game.games;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.util.BukkitUtil;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WoolShuffle extends Game implements Listener {

    private static final long MINIMUM_TIME = TimeUnit.SECONDS.toMillis(2);
    private static final long DEFAULT_TIME = TimeUnit.SECONDS.toMillis(15);
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
        super(GameType.WOOL_SHUFFLE, GameFlag.WATER_ELIMINATE, GameFlag.NO_FALL, GameFlag.NO_PVP);
    }

    @Override
    public void setup() {
        this.currentTime = System.currentTimeMillis() + DEFAULT_TIME;
        this.round = 1;
        chosenColor = getRandomColor();
        posOne = getLocationByName("Pos1");
        posTwo = getLocationByName("Pos2");
        this.cuboid = new Cuboid(posOne, posTwo);

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getLocationByName("Lobby"));

        });

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::startRound, 15L);
    }

    @Override
    public void end() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        setArea(DyeColor.WHITE);

        super.end();
    }

    public void startRound() {
        if (this.state == GameState.ENDED || this.state == GameState.FINISHED) return;
        state = GameState.GRACE_PERIOD;


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
                    this.cancel();
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
        round++;
        this.currentTime = System.currentTimeMillis() + Math.max(MINIMUM_TIME, DEFAULT_TIME / round);
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
                float percentLeft = (float) millisLeft / Math.max(MINIMUM_TIME, DEFAULT_TIME / round);


                getAlivePlayers().forEach(gamePlayer -> {
                    Player player = gamePlayer.toPlayer();
                    player.setExp(percentLeft);
                    player.setLevel(millisLeft / 1_000);
                });
            }

        }.runTaskTimer(Brawl.getInstance(), 1L, 1L);


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
                        block.setData(color.getData());
                        block.setType(Material.WOOL);
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
            player.updateInventory();
        });

        state = GameState.GRACE_PERIOD;
        startRound();
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.DARK_PURPLE + "Round: " + CC.LIGHT_PURPLE + this.round + (state == GameState.STARTED ? CC.GRAY + " (" + TimeUnit.MILLISECONDS.toSeconds(currentTime - System.currentTimeMillis()) + "s)" : ""));
        toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);
        if (this.state == GameState.STARTED) {
            toReturn.add(CC.DARK_PURPLE + "Color: "  + (chosenColor == null ? "None" : BukkitUtil.getFriendlyName(chosenColor)));
        } else if (this.state == GameState.FINISHED) {
            boolean winners = this.winners.size() > 1;
            if (winners) {
                toReturn.add(CC.DARK_PURPLE + "Winners: ");
                for (GamePlayer winner : this.winners) {
                    toReturn.add(CC.LIGHT_PURPLE + "  " + winner.getName());
                }
            } else if (!this.winners.isEmpty()) {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.LIGHT_PURPLE + this.winners.get(0).getName());
            } else {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.RED + "None");
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
