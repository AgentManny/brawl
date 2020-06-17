package rip.thecraft.brawl.visual;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.visual.tasks.HologramUpdateTask;
import rip.thecraft.falcon.hologram.hologram.Hologram;

import java.util.*;

public class VisualManager implements Listener {

    public static final String HOLO_STATS = "HOLO_STATS";
    public static final String HOLO_LB = "HOLO_LB";

    private final Brawl plugin;

    public Map<UUID, Hologram> playerStats = new HashMap<>();

    public VisualManager(Brawl plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new VisualListener(this), plugin);

        new HologramUpdateTask(this).runTaskTimer(plugin, 20L, 120L);
    }

    public List<String> getHoloStats(Player player) {
        List<String> lines = new ArrayList<>();

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        if (playerData == null) return lines;

        lines.add(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Statistics");
        for (StatisticType type : StatisticType.values()) {
            if (type.isHidden()) continue;
            String displayValue = String.valueOf(Math.round(playerData.getStatistic().get(type)));
            if (type == StatisticType.LEVEL) {
                Level level = playerData.getLevel();
                displayValue += " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " EXP)";
            }

            lines.add(ChatColor.WHITE + type.getName() + ": " + ChatColor.LIGHT_PURPLE +  displayValue);
        }
        return lines;
    }

}
