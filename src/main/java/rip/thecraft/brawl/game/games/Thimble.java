package rip.thecraft.brawl.game.games;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Thimble extends Game implements Listener {

    private static final long JUMP_TIME = TimeUnit.SECONDS.toMillis(12);

    private long currentTime;
    private BukkitTask task;

    private int round;

    private int percentageWater;

    Location posOne, posTwo;
    private Cuboid cuboid;

    private List<GamePlayer> alreadyPlayed;

    public Thimble() {
        super(GameType.THIMBLE, GameFlag.NO_FALL, GameFlag.NO_PVP);
    }

    @Override
    public void setup() {
        this.currentTime = System.currentTimeMillis() + JUMP_TIME;
        this.round = 1;
        percentageWater = 100;
        posOne = getLocationByName("Pos1");
        posTwo = getLocationByName("Pos2");
        this.cuboid = new Cuboid(posOne, posTwo);
        resetPlatform();

        this.alreadyPlayed = new ArrayList<>();

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
        super.end();
    }

    public void startRound() {
        if (this.state == GameState.ENDED || this.state == GameState.FINISHED) return;
        state = GameState.GRACE_PERIOD;

        if (task != null) {
            task.cancel();
            task = null;
        }


        if (round != 1) {
            int minWater = round >= 25 ? 5 : 20;
            percentageWater = Math.max(minWater, percentageWater - 5);

            resetPlatform();
        }

        int water = 0;
        for (Location location : cuboid) {
            if (location.getBlock().isLiquid()) {
                water++;
            }
        }
        if (water == 0) {
            resetPlatform();
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
        round++;
        this.currentTime = System.currentTimeMillis() + JUMP_TIME;


        for (GamePlayer alivePlayer : getAlivePlayers()) {
            alivePlayer.toPlayer().teleport(getLocationByName("Jump"));
        }

        state = GameState.STARTED;
        alreadyPlayed.clear();
        this.task = new BukkitRunnable() {

            public void run() {
                if (currentTime < System.currentTimeMillis()) {
                    cancel();
                    for (GamePlayer alivePlayer : getAlivePlayers()) {
                        if (!alreadyPlayed.contains(alivePlayer)) {
                            Player player = alivePlayer.toPlayer();
                            if (player != null) {
                                handleElimination(player, player.getLocation(), GameElimination.OTHER);
                                if (alreadyPlayed.size() == getAlivePlayers().size()) {
                                    startRound();
                                }
                            }
                        }
                    }
                    return;
                }

                int millisLeft = (int) (currentTime - System.currentTimeMillis());
                float percentLeft = (float) millisLeft / JUMP_TIME;


                getAlivePlayers().forEach(gamePlayer -> {
                    Player player = gamePlayer.toPlayer();
                    player.setExp(percentLeft);
                    player.setLevel(millisLeft / 1_000);
                });
            }

        }.runTaskTimer(Brawl.getInstance(), 1L, 1L);
    }

    public void resetPlatform() {
        int y = cuboid.getLowerY(); // Should only be 1 layer so won't matter
        for (int x = cuboid.getLowerX(); x < cuboid.getUpperX(); x++) {
            for (int z = cuboid.getLowerZ(); z < cuboid.getUpperZ(); z++) {
                Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                block.setType(Math.random() * 100 <= percentageWater ? Material.WATER : Material.GOLD_BLOCK);
            }
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Thimble && game.getState() == GameState.STARTED && containsPlayer(player)) {
                GamePlayer gamePlayer = getGamePlayer(player);
                if (gamePlayer.isAlive() && !alreadyPlayed.contains(gamePlayer)) {
                    handleElimination(player, player.getLocation(), GameElimination.DEATH);
                    if (alreadyPlayed.size() == getAlivePlayers().size()) {
                        startRound();
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    public void processMovement(Player player, GamePlayer gamePlayer, Location from, Location to) {
        if (state == GameState.STARTED) {
            Block block = to.getBlock();
            if (block.isLiquid()) {
                to.getBlock().setType(Material.GOLD_BLOCK);
                to.getWorld().spawn(to, Firework.class);
                player.setFallDistance(0);
                player.teleport(getLocationByName("Lobby"));
                if (!alreadyPlayed.contains(gamePlayer)) {
                    alreadyPlayed.add(gamePlayer);
                }

                if (alreadyPlayed.size() == getAlivePlayers().size()) {
                    startRound();
                }
            }
        }
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.WHITE + "Game: " + ChatColor.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.WHITE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.WHITE + "Round: " + CC.LIGHT_PURPLE + this.round + (state == GameState.GRACE_PERIOD ? CC.GRAY + " (" + (getTime() + 1) + "s)" : ""));
        toReturn.add(CC.BLUE + "   ");
        if (this.state == GameState.STARTED) {
            toReturn.add(CC.WHITE + "Players left: " + ChatColor.LIGHT_PURPLE + (getAlivePlayers().size() - alreadyPlayed.size()));
        } else if (this.state == GameState.FINISHED) {
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
}
