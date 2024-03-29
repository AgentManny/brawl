package rip.thecraft.brawl.duelarena.match;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.duelarena.arena.Arena;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.match.data.MatchData;
import rip.thecraft.brawl.duelarena.match.data.PostMatchData;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.util.EloRating;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@RequiredArgsConstructor
public class Match {

    public static final int GRACE_PERIOD = 3;

    private final String id;
    private final QueueType queueType;

    private final Arena arena;

    private final UUID player1, player2;

    private final MatchLoadout loadout;
    private final Kit kit; // Only used for arcade

    private MatchState state;

    private long startedAt = -1;
    private long endedAt = -1;

    private UUID quited;
    private boolean quitEnded = false;

    private BukkitTask task;

    private int matchAmount = 1;

    private MatchData matchData = new MatchData();

    private String winnerName;

    public void setup() {

        state = MatchState.GRACE_PERIOD;

        for (Player other : Bukkit.getOnlinePlayers()) {
            for (Player member : getPlayers()) {
                if (!contains(other)) {
                    member.hidePlayer(other);
                    if (arena.getArenaType() != ArenaType.NORMAL) {
                        other.hidePlayer(member);
                    }
                }
            }
        }

        Location[] corners = arena.getLocations();
        for (int i = 0; i < 2; i++) {
            final int index = i;
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> getPlayers()[index].teleport(corners[index]), 5 * i);
        }

        for (Player member : getPlayers()) {
            if (arena.getArenaType() == ArenaType.ARCADE) {
                member.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 2), true);
            } else {
                loadout.apply(member);
            }
            NametagHandler.reloadPlayer(member, getOpposite(member));
            member.showPlayer(getOpposite(member));
        }

        if (task != null) {
            task.cancel();
        }
        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> task = new MatchStartTask(this, GRACE_PERIOD).runTaskTimer(Brawl.getInstance(), 20L, 20L), 10L);
    }

    public void start() {
        if (state == MatchState.FINISHED) return;

        if (arena.getArenaType() == ArenaType.ARCADE) {
            for (Player member : getPlayers()) {
                kit.apply(member, false, true);
                member.sendMessage(ChatColor.YELLOW + "You have been given " + ChatColor.DARK_PURPLE + ChatColor.BOLD + kit.getName() + ChatColor.YELLOW + " to fight, kill or be killed.");
            }
        }

        state = MatchState.FIGHTING;
        startedAt = System.currentTimeMillis();
    }

    public void finished(Player winner) {
        if (this.state == MatchState.FINISHED) return;
        state = MatchState.FINISHED;
        endedAt = System.currentTimeMillis();

        this.winnerName = winner == null ? "No one" : winner.getName();
        String name = winner == null ? "No one" : winner.getDisplayName();
        final AtomicBoolean again = new AtomicBoolean(false);

        if (winner != null) {
            getMatchData().getWins().put(winner.getUniqueId(), getMatchData().getWins().getOrDefault(winner.getUniqueId(), 0) + 1);

            if (!quitEnded && getMatchData().getWins().get(winner.getUniqueId()) < matchAmount) {
                again.set(true);
            }
        }

        if (arena.getArenaType() == ArenaType.ARCADE && kit != null) {
            for (Player player : getPlayers()) {
                if (player == null) continue;
                kit.getAbilities().forEach(ability -> ability.getTasks().clear(player.getUniqueId()));
            }
        }

        if (again.get()) {
            broadcast(ChatColor.WHITE + name + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the round, they need " + ChatColor.LIGHT_PURPLE + (this.getMatchAmount() - getMatchData().getWins().get(winner.getUniqueId())) + ChatColor.YELLOW + " more to win.");
        } else {
            broadcast(ChatColor.WHITE + name + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the match.");
        }

        boolean newMatch = again.get() && !quitEnded;

        if (!newMatch) {
            endMatch(winner);
        }

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> reset(newMatch), newMatch ? 2L : 90L);
    }

    public void endMatch(Player winner) {
        if (winner != null) {
            Player loser = Bukkit.getPlayer(getOpposite(winner.getUniqueId()));
            if (!quitEnded) {
                winner.hidePlayer(loser);
            }

            PlayerData wd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(winner);
            PlayerData ld = Brawl.getInstance().getPlayerDataHandler().getPlayerData(getOpposite(winner.getUniqueId()));

            wd.getStatistic().add(StatisticType.DUEL_WINS);
            wd.getStatistic().add(StatisticType.DUEL_WIN_STREAK);
            ld.getStatistic().add(StatisticType.DUEL_LOSSES);
            ld.getStatistic().set(StatisticType.DUEL_WIN_STREAK, 0);

            String eloMessage = null;
            if (queueType == QueueType.RANKED) {
                int winnerElo = wd.getStatistic().get(loadout);
                int loserElo = ld.getStatistic().get(loadout);

                EloRating rating = new EloRating();

                int winnerUpdatedElo = rating.calculate2PlayersRating(winnerElo, loserElo, EloRating.EloOutcomeType.WINNER);
                int loserUpdatedElo = rating.calculate2PlayersRating(loserElo, winnerElo, EloRating.EloOutcomeType.LOSER);

                eloMessage = ChatColor.YELLOW + "Updated Elo: " + CC.GREEN + winner.getName() + " " + winnerUpdatedElo + " (+" + (winnerUpdatedElo - winnerElo) + ")" + " " + CC.RED + loser.getName() + " " + loserUpdatedElo + " (-" + (loserElo - loserUpdatedElo) + ")";

                wd.getStatistic().set(loadout, winnerUpdatedElo);
                ld.getStatistic().set(loadout, loserUpdatedElo);
            }


            broadcast(ChatColor.YELLOW + "Winner: " + ChatColor.LIGHT_PURPLE + this.winnerName);
            if (arena.getArenaType() != ArenaType.SUMO) {
                FancyMessage msg = new FancyMessage(ChatColor.YELLOW + "Inventories: ");
                msg.then(ChatColor.GREEN + winnerName + ChatColor.YELLOW + ", ").tooltip(ChatColor.YELLOW + "Click to view " + ChatColor.LIGHT_PURPLE + winnerName + ChatColor.YELLOW + " inventory").command("/viewmatchinv " + id + " " + wd.getUuid());
                msg.then(ChatColor.RED + loser.getName()).tooltip(ChatColor.YELLOW + "Click to view " + ChatColor.LIGHT_PURPLE + loser.getName() + ChatColor.YELLOW + " inventory").command("/viewmatchinv " + id + " " + ld.getUuid());
                for (Player player : getPlayers()) {
                    msg.send(player);
                }
            }
            wd.save();
            ld.save();


            if (eloMessage != null) {
                broadcast(eloMessage);
            }

            String spectators = matchData.getFriendlySpectators();
            if (!spectators.isEmpty()) {
                broadcast(ChatColor.YELLOW + "Spectators (" + matchData.getSpectators().size() + "): " + ChatColor.LIGHT_PURPLE + spectators);
            }

            MatchSnapshot snapshot = new MatchSnapshot(id);
            snapshot.getInventories().putAll(matchData.getInventories());
            Brawl.getInstance().getMatchHandler().addSnapshot(snapshot);
        }

    }

    public void reset(boolean again) {
        for (Player player : getPlayers()) {
            if (player != null) {
                player.setAllowFlight(false);
                player.setFlying(false);
                if (!again) {
                    DuelArena.respawn(player, getArena().getArenaType() != ArenaType.NORMAL);
                } else {
                    if (this.state != MatchState.GRACE_PERIOD) {
                        setup();
                    }
                }
            }
        }

        if (!again) {
            for (UUID uuid : getMatchData().getSpectators()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                player.closeInventory();
                SpectatorMode spectator = Brawl.getInstance().getSpectatorManager().getSpectator(player);
                if (spectator != null) {
                    if (spectator.getFollow() != null) {
                        spectator.spectate(player); // Update spectating
                    } else {
                        spectator.leave();
                    }
                }
            }

            for (Player other : Bukkit.getOnlinePlayers()) {
                Match match = Brawl.getInstance().getMatchHandler().getMatch(other);
                if (match == null) {
                    for (Player member : getPlayers()) {
                        if (member != null) {
                            other.showPlayer(member);
                            if (!other.hasMetadata("hidden")) {
                                member.showPlayer(other);
                            }
                        }
                    }
                }
            }

            for (Player member : getPlayers()) {
                if (member == null) continue;

                Player opponent = Bukkit.getPlayer(this.getOpposite(member.getUniqueId()));
                if (opponent != null) {
                    member.showPlayer(opponent);
                }
            }

            Brawl.getInstance().getMatchHandler().dispose(this);
        }
    }

    @Override
    public int hashCode() {
        return player1.hashCode() ^ player2.hashCode();
    }

    public void quit(Player player) {
        quitEnded = true;
        quited = player.getUniqueId();

        finished(Bukkit.getPlayer(getOpposite(player.getUniqueId())));
        for (Player other : Bukkit.getOnlinePlayers()) {
            Match match = Brawl.getInstance().getMatchHandler().getMatch(other);
            if (match == null) {
                for (Player member : getPlayers()) {
                    if (member != null) {
                        other.showPlayer(member);
                        member.showPlayer(other);
                    }
                }
            }
        }
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);

//        player.teleport(Brawl.getInstance().getLocationByName("DUEL_ARENA"));
    }

    public void eliminated(Player player) {
        if (arena.getArenaType() != ArenaType.SUMO) {
            EntityLightning is = new EntityLightning(((CraftWorld) player.getWorld()).getHandle(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(is);
            for (Player other : getPlayers()) {
                if (other != null) {
                    other.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                    ((CraftPlayer) other).getHandle().playerConnection.sendPacket(packet);
                    player.setItemInHand(null);
                    PlayerUtil.animateDeath(player, other);
                }
            }
        }
        Player opponent = Bukkit.getPlayer(getOpposite(player.getUniqueId()));

        PostMatchData.addData(this, player, opponent);
        finished(opponent);
    }

    public UUID getOpposite(UUID uuid) {
        if (uuid.equals(player1)) {
            return player2;
        }
        return player1;
    }

    public Player getOpposite(Player player) {
        if (player.getUniqueId().equals(player1)) {
            return getPlayers()[1];
        }
        return getPlayers()[0];
    }

    public boolean contains(Player player) {
        return player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2);
    }

    public Player[] getPlayers() {
        return new Player[]{Bukkit.getPlayer(player1), Bukkit.getPlayer(player2)};
    }

    public void playSound(boolean victory) {
        for (Player player : getPlayers()) {
            if (player != null) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1L, victory ? 20L : 1L);
            }
        }
        for (UUID uuid : this.getMatchData().getSpectators()) {
            Player spec = Bukkit.getPlayer(uuid);
            if (spec != null) {
                spec.playSound(spec.getLocation(), Sound.NOTE_PIANO, 1L, victory ? 20L : 1L);
            }
        }
    }

    public void broadcast(String message) {
        for (Player player : getPlayers()) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
        for (UUID uuid : this.getMatchData().getSpectators()) {
            Player spec = Bukkit.getPlayer(uuid);
            if (spec != null) {
                spec.sendMessage(message);
            }
        }
    }

}
