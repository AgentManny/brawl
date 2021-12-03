package rip.thecraft.brawl.duelarena.match;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class MatchStartTask extends BukkitRunnable  {

    private final Match match;
    private int seconds;

    @Override
    public void run() {
        if (match == null || match.isQuitEnded()) {
            cancel();
            return;
        }

        if (seconds <= 0) {
            for (UUID uuid : match.getMatchData().getSpectators()) {
                Player spec = Bukkit.getPlayer(uuid);
                if (spec != null) {
                    for (Player player : match.getPlayers()) {
                        if (player == null) continue;
                        spec.showPlayer(player);
                    }
                }
            }

            for (Player player : match.getPlayers()) {
                if (player == null) {
                    match.finished(null);
                    return;
                }
                player.setFireTicks(0);
            }

            match.playSound(true);
            match.broadcast(ChatColor.YELLOW + "The match has started!");
            match.start();
            this.cancel();
            return;
        }

        match.playSound(false);
        match.broadcast(ChatColor.YELLOW + "The match starts in " + ChatColor.LIGHT_PURPLE + seconds + ChatColor.YELLOW + " second" + (seconds == 1 ? "" : "s") + "...");
        seconds--;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        if (match != null) {
            match.setTask(null);
        }
    }
}
