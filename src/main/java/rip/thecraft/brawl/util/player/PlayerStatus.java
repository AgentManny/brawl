package rip.thecraft.brawl.util.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerStatus {

    FIRE(0x01),
    CROUCHING(0x02),

    SPRINTING(0x08),
    BLOCK(0x16),
    INVISIBLE(0x20),

    ;

    private int bitmask;
}
