package rip.thecraft.brawl.duelarena.queue;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class QueueSearchTask extends BukkitRunnable {

    private final UUID uuid;
    private final PlayerData playerData;

    private final MatchLoadout loadout;

    private long startedAt;
    private int playerElo, maxRange, minRange;

    public QueueSearchTask(Player player, MatchLoadout loadout) {
        this.uuid = player.getUniqueId();
        this.playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        this.loadout = loadout;

        this.startedAt = System.currentTimeMillis();

        this.playerElo = playerData.getStatistic().get(loadout);
        this.maxRange = playerElo + 10;
        this.minRange = playerElo - 10;
    }

    @Override
    public void run() {
        DuelArenaHandler mh = Brawl.getInstance().getMatchHandler();
        Player player;
        if (playerData == null || (player = playerData.getPlayer()) == null || !mh.isInQueue(player) || mh.isInMatch(player)) {
            cancel();
            mh.cleanup(uuid);
            return;
        }


        incrementRange();

        UUID other = mh.getRankedQueue().get(loadout).stream().filter(otherUuid -> otherUuid != uuid && inRange(Brawl.getInstance().getPlayerDataHandler().getPlayerData(otherUuid).getQueueData().getTask()))
                .findAny()
                .orElse(null);

        if (other != null) {
            PlayerData otherData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(other);

            mh.cleanup(other);
            mh.cleanup(uuid);

            if (otherData.getQueueData().getTask() != null) {
                otherData.getQueueData().getTask().cancel();
            }
            otherData.getQueueData().setTask(null);

            mh.createMatch(Bukkit.getPlayer(other), player, loadout, QueueType.RANKED);

            cancel();
            return;
        }

        long sec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startedAt);
        if (sec % 5 == 0) {
            player.sendMessage(CC.YELLOW + "Searching for opponents... " + CC.LIGHT_PURPLE + "[" + minRange + " -> " + maxRange + "]");
        }
    }

    public void incrementRange() {
        if (this.maxRange + 10 >= 5000) {
            this.maxRange = 5000;
        } else {
            this.maxRange = this.maxRange + 10;
        }

        if (this.minRange - 10 <= 0) {
            this.minRange = 0;
        } else {
            this.minRange = this.minRange - 10;
        }
    }

    private boolean inRange(QueueSearchTask other) {
        return (this.playerElo >= other.getMinRange() && this.playerElo <= other.getMaxRange()) || (other.getPlayerElo() >= this.minRange && other.getPlayerElo() <= this.maxRange);
    }

}