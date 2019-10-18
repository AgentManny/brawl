package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Toss extends Ability implements Listener {

    private Map<UUID, Long> times = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Player && hasEquipped(player)) {
            final Player target = (Player)event.getRightClicked();

            if (hasCooldown(player, false)) return;
            addCooldown(player);

            player.setPassenger(target);
            target.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " has picked you up.");
            times.put(player.getUniqueId(), System.currentTimeMillis() + 250L);

            Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), player::eject, 20L * 10); // Releases automatically after 10 seconds
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getPassenger() != null && player.getPassenger() instanceof Player && hasEquipped(player) && times.get(player.getUniqueId()) < System.currentTimeMillis()) {
            Player target = (Player) player.getPassenger();
            player.eject();

            final Vector vector = player.getEyeLocation().getDirection();
            vector.multiply(1.75 / vector.length());
            vector.setY(vector.getY() / 2);
            target.setVelocity(vector);
        }
    }

    @Override
    public void onDeactivate(Player player) {
        times.remove(player.getUniqueId());
    }
}
