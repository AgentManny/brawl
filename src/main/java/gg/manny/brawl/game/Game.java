package gg.manny.brawl.game;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.lobby.GameLobby;
import gg.manny.brawl.game.map.GameMap;
import gg.manny.brawl.game.option.GameOption;
import gg.manny.brawl.game.scoreboard.GameScoreboard;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.brawl.scoreboard.NametagAdapter;
import gg.manny.brawl.util.Tasks;
import gg.manny.pivot.util.PlayerUtils;
import gg.manny.pivot.util.TimeUtils;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public abstract class Game {

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

    private int time;

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
                PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
                NametagAdapter.reloadPlayer(player);
                NametagAdapter.reloadOthersFor(player);
            }
        });
    }


    public void cleanup() {
        startedAt = -1;
        endedAt = -1;

        spectators.clear();

        //todo buggy
        Brawl.getInstance().getSpectatorManager().bug(this);

        players.clear();

        winners.clear();
        map = null;
        options.clear();
    }

    public void end() {
        if (this.state == GameState.FINISHED) return;

        this.endedAt = System.currentTimeMillis();
        this.state = GameState.FINISHED;

        if(!this.winners.isEmpty()) {
            String winners = this.winners.stream().map(GamePlayer::getName).collect(Collectors.joining(", ")).trim();
            for (GamePlayer winner : this.winners) {
                PlayerData playerData = winner.toPlayerData();
                if (playerData != null) {
                    playerData.getStatistic().add(StatisticType.CREDITS, 250);
                }
            }
            Bukkit.broadcastMessage(PREFIX + ChatColor.WHITE + winners + ChatColor.YELLOW + (this.winners.size() <= 1 ? " has" : " have") + " won the " + ChatColor.DARK_PURPLE + getType().getShortName() + ChatColor.YELLOW + " event and received " + ChatColor.LIGHT_PURPLE + "250 credits" + ChatColor.YELLOW + ".");
        }

        Tasks.schedule(() -> {

            this.options.values().forEach(option -> option.onEnd(this));
            this.getAlivePlayers().forEach(GamePlayer::spawn);
            this.spectators.forEach(uuid -> Brawl.getInstance().getSpectatorManager().removeSpectator(uuid, this, false));
            Brawl.getInstance().getGameHandler().destroy();

        }, 60);
    }

    public void setup() {
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(this.getRandomLocation());
        });

        this.startTimer(5, true);
    }

    public void start() {
        this.getOptions().values().forEach(option -> option.onStart(this));
    }

    public boolean eliminate(Player player) {
        GamePlayer eliminated = getGamePlayer(player);
        if (eliminated == null || !eliminated.isAlive() || this.state == GameState.FINISHED) return false;

        eliminated.setAlive(false); // Died
        return true;
    }

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

        }.runTaskTimerAsynchronously(Brawl.getInstance(), 20L, 20L);
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
