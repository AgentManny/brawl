package rip.thecraft.brawl.game.type;

import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;

@Setter
public abstract class RoundGame extends Game {

    public int round = 0;
    public int maxRounds = -1; // Should game end after X amount of rounds

    // Round timing
    private long timeLeft = -1L;
    private long maxRoundTime = -1L;

    // Experience bar
    private boolean expShowTimer = true; // False will indicate rounds instead

    private boolean timeLimitEliminate = true; // Eliminate if they haven't completed round after X time

    public Set<UUID> played = new HashSet<>(); // Players who have completed the round
    private boolean roundCheckAfterEliminate = true; // Should rounds reset after eliminate (or check isRoundOver())

    public long newRoundDelay = 20L; // in ticks

    private BukkitTask task;

    public RoundGame(GameType type, GameFlag... flags) {
        super(type, flags);
    }

    @Override
    public void setup() {
        round = 0;

        teleport();

        startRound();
    }

    @Override
    public void cleanup() {
        round = 0;
        played.clear();

        super.cleanup();
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

        task = Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            onRoundSetup();
            startRoundTimer();
        }, newRoundDelay);
    }

    public void nextRound() {
        if (this.state == GameState.GRACE_PERIOD) return;
        if (task != null) {
            task.cancel();
            task = null;
        }

        onRoundEnd();

        played.clear();
        startRound();
    }

    public void tick() {
        if (maxRoundTime == -1 || timeLeft == -1 || state != GameState.STARTED) return;

        if (timeLeft < System.currentTimeMillis()) {
            if (timeLimitEliminate) {
                for (GamePlayer alivePlayer : getAlivePlayers()) {
                    if (!played.contains(alivePlayer.getUniqueId())) {
                        Player player = alivePlayer.toPlayer();
                        if (player != null) {
                            handleElimination(player, player.getLocation(), GameElimination.OTHER);
                            if (isRoundOver()) {
                                nextRound();
                                return;
                            }
                        }
                    }
                }
            }

            if (state == GameState.STARTED) {
                nextRound();
            }
            return;
        }

        if (expShowTimer) {
            int millisLeft = (int) (timeLeft - System.currentTimeMillis());
            float percentLeft = (float) millisLeft / maxRoundTime;

            getAlivePlayers().forEach(player -> player.setExp(millisLeft / 1000, percentLeft));
        }
    }

    public void setupRound() {
        state = GameState.STARTED;
        onRoundStart();
        setTime(-1);
        timeLeft = System.currentTimeMillis() + maxRoundTime;

        task = Brawl.getInstance().getServer().getScheduler().runTaskTimer(Brawl.getInstance(), this::tick, 20L, 20L);
    }

    public void startRoundTimer() {
        round++;

        getAlivePlayers().forEach(player -> player.setExp(round, 0));

        setTime(3);
        task = new BukkitRunnable() {
            public void run() {
                if (getTime() == 0) {
                    setupRound();
                    cancel();
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

    public boolean isRoundOver() {
        return played.size() == getAlivePlayers().size();
    }

    public abstract void onRoundSetup();

    public abstract void onRoundStart();

    public abstract void onRoundEnd();

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        super.handleElimination(player, location, elimination);
        if (state != GameState.FINISHED) {
            if (isRoundOver()) {
                nextRound();
            }
        }
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(ChatColor.WHITE + "Game: " + ChatColor.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.WHITE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        if (round != 0) {
            long millisLeft = timeLeft - System.currentTimeMillis();
            String timeLeft = maxRoundTime > 0 && state == GameState.STARTED ? ChatColor.GRAY + " (" + DurationFormatter.getTrailing(millisLeft) + "s)" : "";
            toReturn.add(CC.WHITE + (state == GameState.FINISHED ? "Final " : "") + "Round: " + CC.LIGHT_PURPLE + round + timeLeft);
            if (state == GameState.STARTED) {
                toReturn.add(CC.BLUE + "    ");
                toReturn.add(CC.WHITE + "Players left: " + ChatColor.LIGHT_PURPLE + (getAlivePlayers().size() - played.size()));
            }
        }

        if (state == GameState.GRACE_PERIOD) {
            toReturn.add(CC.BLUE + "    ");
            long seconds = time + 1;
            if (seconds == 4 || seconds <= 0) {
                toReturn.add(CC.LIGHT_PURPLE + "Waiting...");
            } else {
                toReturn.add(CC.WHITE + "Starting in " + CC.LIGHT_PURPLE + seconds + "s");
            }
        }

        if (state == GameState.FINISHED) {
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
        }

        return toReturn;
    }
}