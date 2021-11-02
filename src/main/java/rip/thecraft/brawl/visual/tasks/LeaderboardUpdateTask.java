package rip.thecraft.brawl.visual.tasks;

import com.google.common.base.Strings;
import gg.manny.hologram.Hologram;
import gg.manny.hologram.HologramAPI;
import gg.manny.hologram.HologramBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.leaderboard.Leaderboard;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.visual.VisualManager;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.function.BiConsumer;

import static rip.thecraft.brawl.visual.VisualManager.HOLO_LB;

public class LeaderboardUpdateTask extends BukkitRunnable {

    private final VisualManager visualManager;

    private Hologram hologram;

    public LeaderboardUpdateTask(VisualManager visualManager) {
        this.visualManager = visualManager;

        Location loc = Brawl.getInstance().getLocationByName(HOLO_LB).clone();

        this.hologram = new HologramBuilder()
                .id("LEADERBOARDS")
                .location(loc)
                .addLines(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Leaderboards", "Loading...", " ")
                .build();
        hologram.send();

        HologramAPI.register(hologram);
    }
    private StatisticType statType = StatisticType.KILLS;

    private static final int REFRESH_TIMER = 10;
    private int refreshTimer = 0;
    private boolean refreshed = false;

    private DecimalFormat statFormat = new DecimalFormat("#.#");

    private BiConsumer<StatisticType, Hologram> update = (stat, hologram) -> {
        Leaderboard leaderboard = Brawl.getInstance().getLeaderboard();
        hologram.setLine(1, stat.getColor() + stat.getName());

        int entries = 0;
        Map<String, Double> values = leaderboard.getSpawnLeaderboards().get(statType);
        if (values == null) return;
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            ++entries;

            String prefix = entries == 1 ? ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD : entries == 2 ? ChatColor.LIGHT_PURPLE.toString() : entries == 3 ? ChatColor.YELLOW.toString() : ChatColor.WHITE.toString();
            switch (entries) {
                case 1: {
                    prefix = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD;
                    break;
                }
                case 2: {
                    prefix = ChatColor.LIGHT_PURPLE.toString();
                    break;
                }
                case 3: {
                    prefix = ChatColor.YELLOW.toString();
                    break;
                }
            }
            hologram.setLine(entries + 2, (prefix + entries + ". " + ChatColor.WHITE + entry.getKey() + ChatColor.GRAY + " ● " + ChatColor.WHITE + statFormat.format(entry.getValue())));
        }
        if (!refreshed) {
            hologram.addLines(" ");
            hologram.addLines(getProgressBar(refreshTimer, '\u25A0'));
            refreshed = true;
        }
    };

    @Override
    public void run() {
        if (refreshTimer++ >= REFRESH_TIMER) {
            refreshTimer = 0;
            statType = statType.next();
            while (statType == StatisticType.DUEL_LOSSES || statType == StatisticType.DUEL_WINS || statType == StatisticType.DUEL_WIN_STREAK || statType == StatisticType.KDR) {
                statType = statType.next(); // We can't display KDR on leaderboards unfortunately
            }
            update.accept(statType, hologram); // We only update this every refresh timer
        }

        if (refreshed) {
            hologram.setLine(hologram.getLines().size() - 1, getProgressBar(refreshTimer, '\u25A0'));
        }
    }

    private String getProgressBar(int current, char symbol) {
        float percent = (float) current / LeaderboardUpdateTask.REFRESH_TIMER;
        int progressBars = (int) (10 * percent);

        return Strings.repeat("" + ChatColor.LIGHT_PURPLE + symbol, progressBars)
                + Strings.repeat("" + ChatColor.GRAY + symbol, 10 - progressBars);
    }

}
