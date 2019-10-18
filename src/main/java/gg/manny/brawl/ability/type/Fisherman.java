package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.PivotUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class Fisherman extends Ability implements Listener {

    private double range = 15;
    private double speed = 0.425;

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if(this.hasEquipped(player)) {
            if (event.getCaught() instanceof Player) {
                if (this.hasCooldown(player, true)) return;
                this.addCooldown(player);

                Player caught = (Player) event.getCaught();

                float yaw = caught.getLocation().getYaw();
                float pitch = caught.getLocation().getPitch();

                Location loc  = player.getLocation().clone();
                loc.setYaw(yaw);
                loc.setPitch(pitch);

                caught.teleport(loc);

                caught.damage(0, player);
                PivotUtil.run(() ->  caught.setVelocity(new Vector()), false);
            }
        }
    }
}
