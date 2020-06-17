package rip.thecraft.brawl.hologram;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.falcon.hologram.hologram.Hologram;
import rip.thecraft.falcon.hologram.hologram.Holograms;

import java.util.*;

public class HologramManager implements Listener {

    private static final String PLAYER_HOLO_STATS = "LB_HOLO";

    private final Brawl plugin;

    @Getter private Map<UUID, Hologram> playerStats = new HashMap<>();

    public HologramManager(Brawl plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        new HologramUpdateTask(this).runTaskTimer(plugin, 20L, 120L);
    }

    public Hologram createHologram(Player player) {
        return Holograms.forPlayers(Arrays.asList(player))
                .at(plugin.getLocationByName(PLAYER_HOLO_STATS))
                .addLines(getLines(plugin.getPlayerDataHandler().getPlayerData(player)))
                .build();
    }

    public List<String> getLines(PlayerData playerData) {
        List<String> lines = new ArrayList<>();
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Hologram hologram = createHologram(player);
        hologram.send();
        playerStats.put(player.getUniqueId(), hologram);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerStats.get(player.getUniqueId()).destroy();
        playerStats.remove(player.getUniqueId());
    }

}
