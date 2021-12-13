package rip.thecraft.brawl.util.player.datawatcher.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import rip.thecraft.brawl.util.player.datawatcher.DataWatcherHelper;

@Getter
@AllArgsConstructor
public enum EntityLivingDataWatcherHelper implements DataWatcherHelper<LivingEntity> {

    ENTITY_OPTIONS(0), // e.g. Invisibility

    POTION_EFFECT_COLOR(7),
    POTION_EFFECT_ACTIVE(9),

    HEALTH(6)

    ;

    private int id;

}
