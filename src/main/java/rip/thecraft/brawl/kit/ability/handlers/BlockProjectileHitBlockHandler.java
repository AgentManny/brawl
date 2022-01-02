package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import gg.manny.streamline.util.moreprojectiles.event.BlockProjectileHitEvent;

/**
 * Created by Flatfile on 10/21/2021.
 */
public interface BlockProjectileHitBlockHandler extends AbilityHandler {

    boolean onBlockProjectileHitBlock(Player shooter, BlockProjectileHitEvent event);

}
