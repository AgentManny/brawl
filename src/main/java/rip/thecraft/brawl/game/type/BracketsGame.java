package rip.thecraft.brawl.game.type;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BracketsGame extends Game {

    private String kit;

    private GamePlayer player1;
    private GamePlayer player2;

    private int round;

    private List<GamePlayer> alreadyPlayed;

    private BukkitTask task;

    public BracketsGame(GameType type, String kit) {
        super(type, GameFlag.PLAYER_ELIMINATE, GameFlag.NO_FALL);
        this.kit = kit;
    }

    public Kit getKit() {
        return kit == null ? null : Brawl.getInstance().getKitHandler().getKit(kit);
    }

    public RefillType getRefillType(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        return playerData.getRefillType();
    }

    public int getRefillAmount() {
        return 8;
    }

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

        // Teleports existing players back to lobby
        for (GamePlayer player : this.getMatch()) {
            if (player != null && player.toPlayer() != null && player.isAlive()) {
                player.toPlayer().teleport(this.getLocationByName("Lobby"));
                NametagHandler.reloadPlayer(player.toPlayer());
            }
        }


        GamePlayer[] players = createMatch();
        for (GamePlayer gamePlayer : players) {
            Player player = gamePlayer.toPlayer();

            player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "Your opponent is " + ChatColor.LIGHT_PURPLE + getOpposite(gamePlayer).getName() + ChatColor.YELLOW + ".");
            player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0F, 1.0F);
            PlayerUtil.resetInventory(player, GameMode.SURVIVAL);
            NametagHandler.reloadPlayer(player);
        }

        Kit kit = getKit();
        for (int i = 0; i < getMatch().length; i++) {
            GamePlayer gamer = getMatch()[i];
            //Bukkit.broadcastMessage("Is this primary thread teleport: " + Bukkit.isPrimaryThread());
            Player player = gamer.toPlayer();
            player.teleport(getLocationByName("ArenaLocation" + (i + 1)));
            if (kit != null) {
                kit.apply(player, false, false);
            } else {
                PlayerUtil.resetInventory(player);
            }

            ItemStack item = getRefillType(player).getItem();
            for (int refill = 0; refill < getRefillAmount(); refill++) {
                player.getInventory().addItem(item);
            }
        }

        setTime(3);

        task = new BukkitRunnable() {
            public void run() {
                if (state == GameState.FINISHED || player1 == null || player1.toPlayer() == null || player2 == null || player2.toPlayer() == null) {
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
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
    }

    @Override
    public void processMovement(Player player, GamePlayer gamePlayer, Location from, Location to) {
        if (state == GameState.GRACE_PERIOD && contains(gamePlayer)) {
            if (from.distance(to) > 0.1) {
                player.teleport(from);
                player.setFallDistance(0);
            }
        }
    }

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (eliminate(player, location, elimination)) {
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
            handleElimination(player1.toPlayer(), null, GameElimination.OTHER);
            return;
        }

        if(getMatch()[1] == null || !player2.isAlive()) {
            handleElimination(player2.toPlayer(), null, GameElimination.OTHER);
            return;
        }

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

    @Override
    public String handleNametag(Player toRefresh, Player refreshFor) {
        GamePlayer[] match = getMatch();
        if (match != null && match[0] != null && match[1] != null) {
            Player player1 = match[0].toPlayer();
            Player player2 = match[1].toPlayer();
            if (player1 != null && player2 != null) { // Ensures match exists
                // Check if toRefresh is playing in game
                if (player1 == toRefresh || player2 == toRefresh) {
                    if (refreshFor == player1 || refreshFor == player2) {
                        return ChatColor.RED.toString();
                    }
                    return player1 == toRefresh ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
                }
            }
            return ChatColor.LIGHT_PURPLE.toString();
        }
        return super.handleNametag(toRefresh, refreshFor);
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.WHITE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.WHITE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.WHITE + "Round: " + CC.LIGHT_PURPLE + this.round + (this.state == GameState.FINISHED ? "" : (time > 0 ? CC.GRAY + " (" + time + "s)" : "")));
        toReturn.add(ChatColor.BLUE + "     ");
        if (this.state == GameState.STARTED) {

            GamePlayer playerOne = player1;
            GamePlayer playerTwo = player2;
            if (player == player2.toPlayer()) {
                playerOne = player2;
                playerTwo = player1;
            }

            toReturn.add(CC.LIGHT_PURPLE + playerOne.getName() + CC.WHITE + " vs. " + CC.LIGHT_PURPLE + playerTwo.getName());
                toReturn.add(CC.WHITE + "(" + CC.LIGHT_PURPLE + playerOne.getCPS() + "CPS" + CC.WHITE + ") vs. (" + CC.LIGHT_PURPLE + playerTwo.getCPS() + "CPS" + CC.WHITE + ")");
            toReturn.add(CC.WHITE + "(" + CC.LIGHT_PURPLE + playerOne.getPing() + "ms" + CC.WHITE + ") vs. (" + CC.LIGHT_PURPLE + playerTwo.getPing() + "ms" + CC.WHITE + ")");

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
