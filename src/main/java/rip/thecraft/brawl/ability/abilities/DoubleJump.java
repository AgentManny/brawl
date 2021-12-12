package rip.thecraft.brawl.ability.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.GroundHandler;
import rip.thecraft.brawl.ability.handlers.ToggleFlightHandler;
import rip.thecraft.brawl.ability.property.AbilityData;

/**
 * Created by Flatfile on 10/31/2021.
 */
@AbilityData(
        name = "Double Jump",
        icon = Material.IRON_BOOTS
)
public class DoubleJump extends Ability implements GroundHandler, ToggleFlightHandler {

    @Override
    public void onRemove(Player player) {
        if(player != null){
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @Override
    public void onGround(Player player, boolean onGround) {
        if(!hasCooldown(player, false)){
            if(onGround && player.getVelocity().getY() < 0.4){
                player.setAllowFlight(true);
                player.setFlying(false);
            }
        }
    }

    @Override
    public void onFlight(Player player, boolean flying) {
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().multiply(1.5D).setY(0.8D));
        player.setFallDistance(player.getFallDistance() - 10);

        addCooldown(player);
    }

}
