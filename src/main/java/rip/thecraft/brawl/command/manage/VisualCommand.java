package rip.thecraft.brawl.command.manage;

import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.upgrade.UpgradeManager;
import rip.thecraft.brawl.visual.VisualManager;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class VisualCommand {

    private final Brawl plugin;

    @Command(names = "visual setloc stats", permission = "op")
    public void setPlayerStats(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Set player statistics hologram to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        plugin.setLocationByName(VisualManager.HOLO_STATS, player.getLocation());
    }

    @Command(names = "visual setloc lb", permission = "op")
    public void setLeaderboardStats(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Set leaderboard hologram to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        plugin.setLocationByName(VisualManager.HOLO_LB, player.getLocation());
    }

    @Command(names = "visual setloc upgrade", permission = "op")
    public void setUpgradeNPC(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Set Upgrader NPC to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
        plugin.setLocationByName(UpgradeManager.NPC_UPGRADER_LOC, loc);

        NPC npc = plugin.getUpgradeManager().getNpc();
        if (npc.isSpawned()) {
            npc.despawn(DespawnReason.PLUGIN);
            player.sendMessage(ChatColor.GRAY + "Removed pre existing Upgrader NPC");
        }

        npc.spawn(loc);
    }


}
