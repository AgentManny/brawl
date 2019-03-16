package gg.manny.brawl.game;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.game.map.GameMap;
import gg.manny.brawl.game.option.GameOption;
import gg.manny.brawl.game.team.GameTeam;
import gg.manny.pivot.util.TimeUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public abstract class Game {

    private final GameType type;

    private GameState state;
    private GameMap map;

    private long startedAt = -1L;
    private long endedAt = -1L;

    private List<UUID> spectators = new ArrayList<>();
    private List<GameTeam.GamePlayer> players = new ArrayList<>();

    private List<GameTeam.GamePlayer> winners = new ArrayList<>();

    private List<GameOption> options = new ArrayList<>();

    private Location defaultLocation;

    private int time;

    public abstract void onStart();

    public abstract void onEnd(List<String> winners);

    public abstract void onEliminate(Player player);

    public abstract void handleElimination(Player player, Location location, boolean disconnected);

    public abstract void destroy();

    public String getSidebarTitle(Player player) {
        return this.getVariables(Locale.SCOREBOARD_GAME_OTHER_TITLE.format());
    }

    public List<String> getSidebar(Player player) {
        List<String> lines = new ArrayList<>();
        for (String entry : Locale.SCOREBOARD_GAME_OTHER_TITLE.toList()) {
            if ((entry.contains("{STATE:STARTED}") && this.state != GameState.STARTED) || (entry.contains("{STATE:GRACE_PERIOD}") && this.state != GameState.GRACE_PERIOD) || (entry.contains("{STATE:FINISHED}") && this.state != GameState.FINISHED)) {
                continue;
            }

            lines.add(this.getVariables(entry));
        }
        return lines;
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

    public void startTimer(int time, boolean sendMessage) {
        this.state = GameState.GRACE_PERIOD;
        this.time = time;
        new BukkitRunnable() {

            private int seconds = time;
            private final boolean message = sendMessage;

            @Override
            public void run() {
                if (seconds <= 0) {
                    onStart();
                    playSound(Sound.NOTE_PIANO, 1L, 20L);
                    this.cancel();
                    return;
                }

                switch (seconds) {
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
                        if (message) {
                            playSound(Sound.NOTE_PIANO, 1L, 1L);
                            broadcast(getVariables(Locale.SCOREBOARD_GAME_OTHER_TITLE.get().replace("{TIME}", TimeUtils.formatIntoDetailedString(time))));
                        }
                        break;
                }
                seconds--;
            }

        }.runTaskTimerAsynchronously(Brawl.getInstance(), 20L, 20L);
    }

    public List<GameTeam.GamePlayer> getAlivePlayers() {
        return this.getPlayers()
                .stream()
                .filter(GameTeam.GamePlayer::isAlive)
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
        this.getPlayers().forEach(player -> player.toPlayer().playSound(player.toPlayer().getLocation(), sound, one, two));

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                player.playSound(player.getLocation(), sound, one, two);
            }
        });
    }

    public void broadcast(String message) {
        this.getPlayers().forEach(player -> player.toPlayer().sendMessage(message));

        this.getSpectators()
                .forEach(spectator -> {
                    Player player = Bukkit.getPlayer(spectator);
                    if(player != null) {
                        player.sendMessage(message);
                    }
                });
    }

    public void broadcast(FancyMessage message) {
        this.getPlayers().forEach(player -> message.send(player.toPlayer()));

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                message.send(player);
            }
        });
    }

    public void broadcast(BaseComponent... component) {
        this.getPlayers().forEach(player -> {
            player.toPlayer().spigot().sendMessage(component);
        });

        this.getSpectators().forEach(spectator -> {
            Player player = Bukkit.getPlayer(spectator);
            if(player != null) {
                player.spigot().sendMessage(component);
            }
        });
    }

}
