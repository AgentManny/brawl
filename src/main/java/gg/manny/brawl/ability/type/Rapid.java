package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class Rapid extends Ability implements Listener {

    private double speed = 1.05;

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getEntity().getShooter();
        event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(this.speed));
    }
}
