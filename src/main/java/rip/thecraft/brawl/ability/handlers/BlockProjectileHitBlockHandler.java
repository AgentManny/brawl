package rip.thecraft.brawl.ability.handlers;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.moreprojectiles.event.BlockProjectileHitEvent;

/**
 * Created by Flatfile on 10/21/2021.
 */
public interface BlockProjectileHitBlockHandler extends AbilityHandler {

    boolean onBlockProjectileHitBlock(Player shooter, BlockProjectileHitEvent event);

}
