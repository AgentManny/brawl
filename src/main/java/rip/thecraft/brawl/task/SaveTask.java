package rip.thecraft.brawl.task;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;

import java.util.concurrent.TimeUnit;

public class SaveTask extends BukkitRunnable {

    private final Brawl plugin;

    public SaveTask(Brawl plugin) {
        this.plugin = plugin;

        this.runTaskTimerAsynchronously(plugin, TimeUnit.MINUTES.toSeconds(5L), TimeUnit.MINUTES.toSeconds(10) * 20L);
    }

    @Override
    public void run() {
        String preparing = "Preparing to save data to mongo...";
        String saved = "Saved all data to mongo";

        long timeTaken = System.currentTimeMillis();

        plugin.getLogger().info(preparing);
        Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + preparing);

        plugin.getTeamHandler().save(false);
        plugin.getPlayerDataHandler().save(false);

        plugin.getLogger().info(saved);
        Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + saved + " in " + ChatColor.YELLOW + (System.currentTimeMillis() - timeTaken) + "ms" + ChatColor.LIGHT_PURPLE + ".");
    }


}
