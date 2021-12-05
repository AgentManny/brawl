package rip.thecraft.brawl.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.game.map.GameMap;
import rip.thecraft.brawl.game.option.GameOption;
import rip.thecraft.brawl.game.scoreboard.GameScoreboard;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.levels.ExperienceType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.brawl.util.SchedulerUtil;
import rip.thecraft.brawl.util.Tasks;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.PlayerUtils;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public abstract class Game {

    public static final int HOST_CREDITS = 300;

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Event] " + ChatColor.WHITE;
    public static final String PREFIX_ERROR = ChatColor.DARK_RED + "[Event] " + ChatColor.WHITE;

    private final GameType type;

    protected GameState state;
    private GameMap map;

    private long startedAt = -1L;
    private long endedAt = -1L;

    private List<UUID> spectators = new CopyOnWriteArrayList<>();
    protected List<GamePlayer> players = new ArrayList<>();

    protected List<GamePlayer> winners = new ArrayList<>();

    private Map<Class<?>, GameOption> options = new HashMap<>();
    protected Set<GameFlag> flags = EnumSet.noneOf(GameFlag.class);

    private Location defaultLocation;

    protected int time;

    public Game(GameType type, GameFlag... flags) {
        this.type = type;
        this.flags.addAll(Arrays.asList(flags));
    }

    public void init(GameLobby lobby) {
        state = GameState.GRACE_PERIOD;
        lobby.getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                GamePlayer gamePlayer = new GamePlayer(player);
                gamePlayer.setAlive(true);
                this.players.add(gamePlayer);
                PlayerUtil.resetInventory(player, GameMode.SURVIVAL);
                NametagHandler.reloadPlayer(player);
                NametagHandler.reloadOthersFor(player);
            }
        });
        defaultLocation = getLocationByName("Lobby");
    }

    public void addItems(Player player) {

    }

    public String getEliminateMessage(Player player, GameElimination elimination) {
        return ChatColor.DARK_RED + player.getName() + ChatColor.RED + " " + elimination.getMessage() + ".";
    }

    public void cleanup() {
        startedAt = -1;
        endedAt = -1;
        time = -1;

        spectators.clear();

        players.clear();

        winners.clear();
        map = null;
        options.clear();
    }

    public void end() {
        end(false);
    }

    public void end(boolean force) {
        if (this.state == GameState.FINISHED) return;

        this.endedAt = System.currentTimeMillis();
        this.state = GameState.FINISHED;

        if(!this.winners.isEmpty()) {
            String winners = this.winners.stream().map(GamePlayer::getName).collect(Collectors.joining(", ")).trim();
            for (GamePlayer winner : this.winners) {
                PlayerData playerData = winner.toPlayerData();
                if (playerData != null) {
                    EconUtil.deposit(playerData, type.getCreditsReward());
                    playerData.getLevel().addExp(playerData.getPlayer(), ExperienceType.EVENT_WIN.getExperience() * players.size(), ExperienceType.EVENT_WIN, type.getName());

                    for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
                        if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.GAME_WINS) {
                            challenge.increment(winner.toPlayer(), 1);
                        }
                    }
                    playerData.getStatistic().add(StatisticType.EVENT_WINS);
                }
            }

            Bukkit.broadcastMessage(PREFIX + ChatColor.WHITE + winners + ChatColor.YELLOW + (this.winners.size() <= 1 ? " has" : " have") + " won the "
                    + ChatColor.DARK_PURPLE + getType().getShortName() + ChatColor.YELLOW + " event and received " + ChatColor.LIGHT_PURPLE +
                    type.getCreditsReward()  + " credits" + ChatColor.YELLOW + ".");
        }

        Runnable endTask = () -> {
            this.options.values().forEach(option -> option.onEnd(this));
            this.getAlivePlayers().forEach(GamePlayer::spawn);

            Brawl.getInstance().getSpectatorManager().removeSpectators(SpectatorMode.SpectatorType.GAME, this);

            clear();
            Brawl.getInstance().getGameHandler().destroy();
        };
        if (force) {
            endTask.run();
        } else {
            Tasks.schedule(() -> {

                this.options.values().forEach(option -> option.onEnd(this));
                this.getAlivePlayers().forEach(GamePlayer::spawn);

                Brawl.getInstance().getSpectatorManager().removeSpectators(SpectatorMode.SpectatorType.GAME, this);

                clear();
                Brawl.getInstance().getGameHandler().destroy();

            }, 60);
        }

        Tasks.schedule(() -> {

            this.options.values().forEach(option -> option.onEnd(this));
            this.getAlivePlayers().forEach(GamePlayer::spawn);

            Brawl.getInstance().getSpectatorManager().removeSpectators(SpectatorMode.SpectatorType.GAME, this);

            clear();
            Brawl.getInstance().getGameHandler().destroy();

        }, force ? 0 : 60);
    }

    public void clear() {

    }

    public void setup() {
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            addItems(player);
            player.updateInventory();
            player.teleport(this.getRandomLocation());
        });

        this.startTimer(5, true);
    }

    public void start() {
        this.getOptions().values().forEach(option -> option.onStart(this));
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData != null) {
                for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
                    if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.GAMES) {
                        challenge.increment(player, 1);
                    }
                }
            }
        });
    }

    public boolean eliminate(Player player, Location location, GameElimination elimination) {
        GamePlayer eliminated = getGamePlayer(player);
        if (eliminated == null || !eliminated.isAlive() || this.state == GameState.FINISHED) return false;
        eliminated.setAlive(false); // Died
        player.getInventory().setItemInHand(null);

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.setEvent(false); // No longer in event (doesn't count if spectating event)
        if (elimination == GameElimination.LEFT) {
            eliminated.spawn();
        } else if (elimination != GameElimination.QUIT) { // Add to spectator
            PlayerUtils.animateDeath(player);
            SchedulerUtil.runTaskLater(() -> {
                SpectatorMode spectatorMode = Brawl.getInstance().getSpectatorManager().addSpectator(player, location);
                spectatorMode.spectate(SpectatorMode.SpectatorType.GAME, false);
                for (int i = 0; i < 8; i++) { // Only add the LEAVE SPECTATOR item (in the 9th slot)
                    player.getInventory().setItem(i, null);
                }
            }, elimination == GameElimination.PLAYER ? 15L : 5L, false);
        }

        broadcast(getEliminateMessage(player, elimination));
        return true;
    }

    public void processMovement(Player player, GamePlayer gamePlayer, Location from, Location to) {

    }

    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (eliminate(player, location, elimination)) {
            // Find a winner
            if (this.getAlivePlayers().size() == 1) {
                GamePlayer winner = this.getAlivePlayers().get(0);
                this.winners.add(winner);

                this.end(false);
            }
        }
    }

    public String getSidebarTitle(Player player) {
        return this.type.getShortName().toUpperCase();
    }

    public List<String> getSidebar(Player player) {
        return GameScoreboard.getDefault(this);
    }

    public String handleNametag(Player toRefresh, Player refreshFor) {
        if (spectators.contains(toRefresh.getUniqueId())) {
            return CC.GRAY;
        }
        return CC.LIGHT_PURPLE;
    }

    public String getVariables(String entry) {
        return entry.replace("{NAME}", this.type.getName())
                .replace("{NAME:SHORT}", this.type.getShortName())
                .replace("{NAME:CAPITALISE}", this.type.getName().toUpperCase())
                .replace("{NAME:SHORT:CAPITALISE}", this.type.getShortName().toUpperCase())
                .replace("{STATE}", WordUtils.capitalizeFully(this.state.name().toLowerCase().replace("_", " ")))
                .replace("{TIME}", this.time + "s")
                .replace("{ALIVE}", this.getAlivePlayers().size() + "")
                .replace("{TOTAL}", this.getPlayers().size() + "")
                .replace("{SPECTATORS}", this.spectators.size() + "")
                ;
    }

    public void startTimer(int time, final boolean sendMessage) {
        this.state = GameState.GRACE_PERIOD;
        this.time = time;
        new BukkitRunnable() {


            @Override
            public void run() {
                if (getTime() <= 0) {
                    state = GameState.STARTED;
                    startedAt = System.currentTimeMillis();

                    broadcast(Game.PREFIX + ChatColor.WHITE + type.getName() + ChatColor.YELLOW + " has now started.");
                    start();
                    playSound(Sound.NOTE_PIANO, 1L, 20L);
                    this.cancel();
                    return;
                }

                switch (getTime()) {
                    case 60:
                    case 30:
                    case 20:
                    case 15:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        if (sendMessage) {
                            playSound(Sound.NOTE_PIANO, 1L, 1L);
                            broadcast(Game.PREFIX + ChatColor.WHITE + type.getName() + ChatColor.YELLOW + " will start in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(getTime()) + ChatColor.YELLOW + ".");
                        }
                        break;
                }
                setTime(getTime() - 1);
            }

        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
    }

    public List<GamePlayer> getAlivePlayers() {
        return this.getPlayers()
                .stream()
                .filter(GamePlayer::isAlive)
                .collect(Collectors.toList());
    }

    public Location getLocationByName(String locationName) {
        if(map != null) {
            return map.getLocations().get(locationName);
        }
        return null;
    }

    public Location getRandomLocation() {
        return this.map.getLocations().values()
                .stream()
                .skip((int) (this.map.getLocations().values().size() * Math.random()))
                .findFirst()
                .orElse(null);
    }

    public void teleport() {
        teleport(getDefaultLocation());
    }

    public void teleport(Location location) {
        this.getAlivePlayers().forEach(gamePlayer -> {
            if (!this.spectators.contains(gamePlayer.getUniqueId())) {
                Player player = gamePlayer.toPlayer();
                if (player != null) {
                    player.teleport(location);
                }
            }
        });
    }

    public void playSound(Location location, Sound sound, float volume, float pitch) {
        this.getAlivePlayers().forEach(player -> {
            if (!this.spectators.contains(player.getUniqueId())) {
                if (player.toPlayer() != null) {
                    player.toPlayer().playSound(location, sound, volume, pitch);
                }
            }
        });

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                player.playSound(location, sound, volume, pitch);
            }
        });
    }

    public void playSound(Sound sound, float one, float two) {
        this.getAlivePlayers().forEach(player -> {
            if (!this.spectators.contains(player.getUniqueId())) {
                if (player.toPlayer() != null) {
                    player.toPlayer().playSound(player.toPlayer().getLocation(), sound, one, two);
                }
            }
        });

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                player.playSound(player.getLocation(), sound, one, two);
            }
        });
    }

    public void broadcast(String message) {
        this.getAlivePlayers().forEach(player -> {
            if (!this.spectators.contains(player.getUniqueId())) {
                if (player.toPlayer() != null) {
                    player.toPlayer().sendMessage(message);
                }
            }
        });

        this.getSpectators()
                .forEach(spectator -> {
                    Player player = Bukkit.getPlayer(spectator);
                    if(player != null) {
                        player.sendMessage(message);
                    }
                });
    }

    public void broadcast(FancyMessage message) {
        this.getAlivePlayers().forEach(player -> {
            if (!this.spectators.contains(player.getUniqueId())) {
                if (player.toPlayer() != null) {
                    message.send(player.toPlayer());
                }
            }
        });

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {

                message.send(player);
            }
        });
    }

    public void broadcast(BaseComponent... component) {
        this.getAlivePlayers().forEach(player -> {
            if (!this.spectators.contains(player.getUniqueId())) {
                player.toPlayer().spigot().sendMessage(component);
            }
        });

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                player.spigot().sendMessage(component);
            }
        });
    }

    public boolean containsPlayer(Player player) {
        if (player != null) {
            for (GamePlayer gamePlayer : this.players) {
                if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAlive(Player player) {
        if (player != null) {
            for (GamePlayer gamePlayer : this.players) {
                if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                    return gamePlayer.isAlive();
                }
            }
        }
        return false;
    }


    public GamePlayer getGamePlayer(Player player) {
        if (player == null) return null;
        for (GamePlayer gamePlayer : this.players) {
            if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                return gamePlayer;
            }
        }
        return null;
    }

    public boolean containsOption(Class<?> option) {
        return this.options.containsKey(option);
    }

    public void addOption(GameOption option) {
        this.options.put(option.getClass(), option);
    }

}
