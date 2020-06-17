package rip.thecraft.brawl.game.type;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.spartan.util.PlayerUtils;
import rip.thecraft.spartan.util.TimeUtils;
import rip.thecraft.server.util.chatcolor.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class Sumo extends Game implements Listener {

    public Sumo() {
        super(GameType.SUMO, GameFlag.WATER_ELIMINATE, GameFlag.NO_FALL);
    }

    private GamePlayer player1;
    private GamePlayer player2;

    private int round;

    private List<GamePlayer> alreadyPlayed;

    private BukkitTask task;

    @Override
    public void setup() {
        this.round = 0;
        this.alreadyPlayed = new LinkedList<>();
        Collections.shuffle(players);

        this.setDefaultLocation(this.getLocationByName("SpectatorLobby"));

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(this.getLocationByName("Lobby"));
        });

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::startRound, 60L);
    }

    public void startRound() {
        state = GameState.GRACE_PERIOD;
        round++;

        if (task != null) {
            task.cancel();
            task = null;
        }

        for (GamePlayer player : this.getMatch()) {
            if (player != null && player.toPlayer() != null) {
                player.toPlayer().teleport(this.getLocationByName("Lobby"));
            }
        }


        GamePlayer[] players = createMatch();
        for (GamePlayer gamePlayer : players) {
            Player player = gamePlayer.toPlayer();

            player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "Your opponent is " + ChatColor.LIGHT_PURPLE + getOpposite(gamePlayer).getName() + ChatColor.YELLOW + ".");
            player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0F, 1.0F);
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        }

        setTime(3);

        task = new BukkitRunnable() {
            public void run() {
                if (player1 == null || player1.toPlayer() == null || player2 == null || player2.toPlayer() == null) {
                    cancel();
                    return;
                }

                if (getTime() == 0) {
                    startMatch();
                    setTime(-1);
                    this.cancel();
                    return;
                }
                switch (getTime()) {
                    case 3:
                    case 2:
                    case 1:

                        playSound(Sound.NOTE_PIANO, 1L, 1L);
                        broadcast(Game.PREFIX + ChatColor.WHITE + (round == 1 ? "First Round" : (getAlivePlayers().size()  < 2 ? "Final Round" : "Round #" + round)) + ChatColor.YELLOW + " will start in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(getTime()) + ChatColor.YELLOW + ".");
                        break;
                    default:
                        break;
                }

                setTime(getTime() - 1);
            }
        }.runTaskTimerAsynchronously(Brawl.getInstance(), 20L, 20L);
    }

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (eliminate(player)) {
            broadcast(ChatColor.DARK_RED + player.getName() + ChatColor.RED + (elimination == GameElimination.QUIT ? " disconnected" : " has been eliminated") + ".");
            if (elimination != GameElimination.QUIT) {
                Brawl.getInstance().getSpectatorManager().addSpectator(player, this);
                player.teleport(this.getRandomLocation());
            }

            // Find a winner
            if (this.getAlivePlayers().size() == 1) {
                GamePlayer winner = this.getAlivePlayers().get(0);
                this.winners.add(winner);

                this.end();
            } else {
                if (contains(getGamePlayer(player))) {
                    this.alreadyPlayed.addAll(Arrays.asList(this.getMatch()));
                    this.startRound();
                }
            }
        }
    }

    public void startMatch() {
        this.state = GameState.STARTED;

        this.broadcast(Game.PREFIX + ChatColor.LIGHT_PURPLE + this.getMatch()[0].getName() + ChatColor.YELLOW + " vs " + ChatColor.LIGHT_PURPLE + this.getMatch()[1].getName());

        if(getMatch()[0] == null || !player1.isAlive()) {
            this.eliminate(player1.toPlayer());
            return;
        }

        if(getMatch()[1] == null || !player2.isAlive()) {
            this.eliminate(player2.toPlayer());
            return;
        }

        getMatch()[0].toPlayer().teleport(this.getLocationByName("ArenaLocation1"));
        getMatch()[1].toPlayer().teleport(this.getLocationByName("ArenaLocation2"));
        PlayerUtils.resetInventory(getMatch()[0].toPlayer(), GameMode.SURVIVAL);
        PlayerUtils.resetInventory(getMatch()[1].toPlayer(), GameMode.SURVIVAL);

        this.playSound(Sound.NOTE_PIANO, 1L, 20L);
    }

    public GamePlayer[] createMatch() {
        List<GamePlayer> playing = this.getAlivePlayers().stream().filter(u -> !this.alreadyPlayed.contains(u) && u.toPlayer() != null).collect(Collectors.toList());

        if(playing.isEmpty() || playing.size() <= 1) {
            this.alreadyPlayed.clear();
            playing = this.getAlivePlayers().stream().filter(u -> u.toPlayer() != null).collect(Collectors.toList());
        }

        GamePlayer player1 = playing.get(Brawl.RANDOM.nextInt(playing.size()));

        playing.remove(player1);

        GamePlayer player2 = playing.get(Brawl.RANDOM.nextInt(playing.size()));
        this.player1 = player1;
        this.player2 = player2;
        return new GamePlayer[] { player1, player2 };

    }

    public GamePlayer[] getMatch() {
        return new GamePlayer[] { player1, player2 };
    }

    public GamePlayer getOpposite(GamePlayer player) {
        if (player.equals(player1)) {
            return player2;
        }
        return player1;
    }

    public boolean contains(GamePlayer player) {
        return player.equals(player1) || player.equals(player2);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Sumo) {
                GamePlayer gamePlayer = this.getGamePlayer(player);
                if (gamePlayer != null) {
                    if (gamePlayer.isAlive()) {
                        if (this.state == GameState.GRACE_PERIOD) {
                            event.setCancelled(true);
                            return;
                        }

                        if (this.contains(gamePlayer)) {
                            event.setDamage(0);
                        } else {
                            event.setCancelled(true);
                        }

                    }
                }
            }
        }
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.DARK_PURPLE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.DARK_PURPLE + "Round: " + CC.LIGHT_PURPLE + this.round + (this.state == GameState.FINISHED ? "" : (getTime() >= 0 ? CC.GRAY + " (" + getTime() + "s)" : "")));
        toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);
        if (this.state == GameState.STARTED) {

            GamePlayer playerOne = player1;
            GamePlayer playerTwo = player2;
            if (player == player2.toPlayer()) {
                playerOne = player2;
                playerTwo = player1;
            }

            toReturn.add(CC.DARK_PURPLE + playerOne.getName() + CC.LIGHT_PURPLE + " vs. " + CC.DARK_PURPLE + playerTwo.getName());
            toReturn.add(CC.DARK_PURPLE + "(" + CC.LIGHT_PURPLE + playerOne.getCPS() + "CPS" + CC.DARK_PURPLE + ") vs. (" + CC.LIGHT_PURPLE + playerTwo.getCPS() + "CPS" + CC.DARK_PURPLE + ")");
            toReturn.add(CC.DARK_PURPLE + "(" + CC.LIGHT_PURPLE + playerOne.getPing() + "ms" + CC.DARK_PURPLE + ") vs. (" + CC.LIGHT_PURPLE + playerTwo.getPing() + "ms" + CC.DARK_PURPLE + ")");

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
}
