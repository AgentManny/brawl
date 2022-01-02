package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import gg.manny.streamline.util.moreprojectiles.event.BlockProjectileHitEvent;

/**
 * Created by Flatfile on 10/19/2021.
 */
public interface BlockProjectileHitHandler extends AbilityHandler {

    boolean onBlockProjectileHit(Player shooter, Player hit, BlockProjectileHitEvent event);

}
