package rip.thecraft.brawl.ability.abilities.classic;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.util.SchedulerUtil;

@AbilityData(color = ChatColor.BLUE)
public class Fisherman extends Ability implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            if (hasEquipped(player)) {
                if (hasCooldown(player, true)) return;
                addCooldown(player);

                Player caught = (Player) event.getCaught();

                float yaw = caught.getLocation().getYaw();
                float pitch = caught.getLocation().getPitch();

                Location loc = player.getLocation().clone();
                loc.setYaw(yaw);
                loc.setPitch(pitch);

                caught.teleport(loc);

                caught.damage(0, player);
                SchedulerUtil.runTask(() -> caught.setVelocity(new Vector()), false);
            }
        }
    }
}