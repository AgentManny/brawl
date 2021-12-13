package rip.thecraft.brawl.util.player.datawatcher.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.player.datawatcher.DataWatcherHelper;

@Getter
@AllArgsConstructor
public enum PlayerStatusDataWatcherHelper implements DataWatcherHelper<Player> {


    FIRE(0x01),
    CROUCHING(0x02),

    SPRINTING(0x08),
    BLOCK(0x16),
    INVISIBLE(0x20),

    ;


    private int bitmask;


}
