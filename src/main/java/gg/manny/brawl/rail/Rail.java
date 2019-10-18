package gg.manny.brawl.rail;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.util.PlayerUtil;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rail implements Listener {

    private Map<UUID, Double> reachMap = new HashMap<>();

    public Rail() {
        Pivot.getInstance().getQuantum().registerCommand(this);
        Brawl.getInstance().getServer().getPluginManager().registerEvents(this, Brawl.getInstance());
    }

    //nasty code

    @Command(names = "rail", permission = "op", description = "suck my dick nigga")
    public void execute(Player sender, double distance) {
        if (distance <= 0) {
            sender.sendMessage(ChatColor.RED + "Reset to normal distance.");
            reachMap.put(sender.getUniqueId(), 0d);
            return;
        }
        reachMap.put(sender.getUniqueId(), distance);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            Player hit = PlayerUtil.getPlayerByEyeLocation(player, reachMap.getOrDefault(player.getUniqueId(), 0.0));
            if (hit != null) {
                ((CraftPlayer)player).getHandle().attack(((CraftPlayer)hit).getHandle());
            }

        }
    }

}
