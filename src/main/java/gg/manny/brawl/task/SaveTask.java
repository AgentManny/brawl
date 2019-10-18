package gg.manny.brawl.task;

import gg.manny.brawl.Brawl;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {

    private final Brawl plugin;

    public SaveTask(Brawl plugin) {
        this.plugin = plugin;

        this.runTaskTimerAsynchronously(plugin, 6000L, 6000L);
    }

    @Override
    public void run() {
        String preparing = "Preparing to save data to mongo";
        String saved = "Saved all data to mongo";

        plugin.getLogger().info(preparing);
        Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + preparing);

        plugin.getTeamHandler().save(false);
        plugin.getPlayerDataHandler().save(false);

        plugin.getLogger().info(saved);
        Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + saved);
    }


}
