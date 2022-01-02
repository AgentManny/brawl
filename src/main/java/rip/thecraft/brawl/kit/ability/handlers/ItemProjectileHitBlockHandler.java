package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import gg.manny.streamline.util.moreprojectiles.event.ItemProjectileHitEvent;

/**
 * Created by Flatfile on 10/21/2021.
 */
public interface ItemProjectileHitBlockHandler extends AbilityHandler {

    boolean onItemProjectileHitBlock(Player shooter, ItemProjectileHitEvent event);

}
