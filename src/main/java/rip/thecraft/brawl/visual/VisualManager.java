package rip.thecraft.brawl.visual;

import gg.manny.hologram.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.leaderboard.Leaderboard;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.visual.tasks.HologramUpdateTask;
import rip.thecraft.brawl.visual.tasks.LeaderboardUpdateTask;

import java.util.*;

public class VisualManager implements Listener {

    public static final String HOLO_STATS = "HOLO_STATS";
    public static final String HOLO_STATS_ELO = "HOLO_STATS_ELO";
    public static final String HOLO_LB = "HOLO_LB";

    private final Brawl plugin;
    private final Leaderboard leaderboard;

    public Map<UUID, Hologram> playerStats = new HashMap<>();

    public VisualManager(Brawl plugin) {
        this.plugin = plugin;
        this.leaderboard = plugin.getLeaderboard();

        plugin.getServer().getPluginManager().registerEvents(new VisualListener(this), plugin);

        new HologramUpdateTask(this).runTaskTimer(plugin, 20L, 200L);

        if (plugin.getLocationByName(HOLO_LB) != null) {
            new LeaderboardUpdateTask(this).runTaskTimer(plugin, 20L, 20L);
        } else {
            plugin.getLogger().warning("[Visual] Leaderboard hologram could not be enabled because location is not set.");
        }
    }

    public String[] getHoloStats(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Statistics");

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        if (playerData == null) {
            lines.add("Loading...");
            return lines.toArray(new String[]{});
        }


        for (StatisticType type : StatisticType.values()) {
            if (type.isHidden()) continue;
            String displayValue = String.valueOf(Math.round(playerData.getStatistic().get(type)));
            if (type == StatisticType.LEVEL) {
                Level level = playerData.getLevel();
                displayValue += " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " EXP)";
            }

            lines.add(ChatColor.WHITE + type.getName() + ": " + ChatColor.LIGHT_PURPLE +  displayValue);
        }
        return lines.toArray(new String[] { });
    }

}
