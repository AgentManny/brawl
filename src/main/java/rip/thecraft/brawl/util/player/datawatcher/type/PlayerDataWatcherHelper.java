package rip.thecraft.brawl.util.player.datawatcher.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.player.datawatcher.DataWatcherHelper;

@Getter
@AllArgsConstructor
public enum PlayerDataWatcherHelper implements DataWatcherHelper<Player> {

    PLAYER_STATE(0), // Crouch 1,
    SKIN_FLAGS(10), // Toggle Skin flags
    TOGGLE_CAPE(16), // Toggle Cape (Hide = 0x02)
    ABSORPTION_HEARTS(17),
    SCORE(18)

    ;

    private int id;


}
