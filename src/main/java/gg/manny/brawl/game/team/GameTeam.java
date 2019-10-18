package gg.manny.brawl.game.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class GameTeam<T extends GamePlayer> {

    @NonNull
    private T leader;

    private List<T> gamePlayers = new ArrayList<>();

    public boolean isLeader(Player player) {
        return this.leader != null && this.leader.getUniqueId().equals(player.getUniqueId());
    }

    public boolean containsPlayer(Player player) {
        for (T gamePlayer : this.gamePlayers) {
            if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public GamePlayer getGamePlayer(Player player) {
        for (T gamePlayer : this.gamePlayers) {
            if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                return gamePlayer;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        this.gamePlayers.forEach(matchPlayer -> {
            Player player = matchPlayer.toPlayer();

            if (player != null) {
                players.add(player);
            }
        });

        return players;
    }

    /**
     * Returns a list of objects that extend {@link GamePlayer} whose
     * {@link GamePlayer#isAlive()} returns true.
     *
     * @return A list of team players that are alive.
     */
    public List<T> getAliveGamePlayers() {
        List<T> alive = new ArrayList<>();

        this.gamePlayers.forEach(player -> {
            if (player.isAlive()) {
                alive.add(player);
            }
        });

        return alive;
    }

    /**
     * Returns an integer that is incremented for each {@link GamePlayer}
     * element in the {@code gamePlayers} list whose {@link GamePlayer#isAlive()}
     * returns true.
     *
     * Use this method rather than calling {@link List#size()} on
     * the result of {@code getAliveGamePlayers}.
     *
     * @return The count of team players that are alive.
     */
    public int getAliveCount() {
        if (this.gamePlayers.size() == 1) {
            return this.leader.isAlive() ? 1 : 0;
        } else {
            int alive = 0;

            for (T player : this.gamePlayers) {
                if (player.isAlive()) {
                    alive++;
                }
            }

            return alive;
        }
    }

    /**
     * Returns a list of objects that extend {@link GamePlayer} whose
     * {@link GamePlayer#isAlive()} returns false.
     *
     * @return A list of team players that are dead.
     */
    public List<T> getDeadGamePlayers() {
        List<T> dead = new ArrayList<>();
        this.gamePlayers.forEach(player -> {
            if (!player.isAlive()) {
                dead.add(player);
            }
        });

        return dead;
    }
    
    public int getDeadCount() {
        return this.gamePlayers.size() - this.getAliveCount();
    }

    public void broadcast(String messages) {
        this.getPlayers().forEach(player -> player.sendMessage(messages));
    }

    public void broadcast(FancyMessage message) {
        this.getPlayers().forEach(message::send);
    }

    public void broadcast(List<String> messages) {
        this.getPlayers().forEach(player -> messages.forEach(player::sendMessage));
    }

    public void broadcast(BaseComponent... component) {
        this.getPlayers().forEach(player -> player.spigot().sendMessage(component));
    }


}
