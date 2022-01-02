package rip.thecraft.brawl.kit.ability.handlers;

import org.bukkit.entity.Player;
import gg.manny.streamline.util.moreprojectiles.event.ItemProjectileHitEvent;

/**
 * Created by Flatfile on 10/19/2021.
 */
public interface ItemProjectileHitHandler extends AbilityHandler {

    boolean onItemProjectileHit(Player shooter, Player hit, ItemProjectileHitEvent event);

}
