package rip.thecraft.brawl.kit.ability.abilities.classic;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.util.SchedulerUtil;

@AbilityData(
        color = ChatColor.BLUE,
        icon = Material.FISHING_ROD,
        displayIcon = false
)
public class Fisherman extends Ability implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            if (hasEquipped(player)) {
                if (hasCooldown(player, true)) return;
                addCooldown(player);

                Player caught = (Player) event.getCaught();

                if(!canAttack(caught)){
                    event.setCancelled(true);
                    return;
                }

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

    @EventHandler
    public void onFishLaunch(ProjectileLaunchEvent event){
        if(event.getEntity() instanceof FishHook){
            if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player){
                Player player = (Player) event.getEntity().getShooter();

                if(hasCooldown(player, true)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean canAttack(Player player){
        return !RegionType.SAFEZONE.appliesTo(player.getLocation());
    }

}