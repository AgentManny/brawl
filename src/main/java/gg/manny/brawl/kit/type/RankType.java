package gg.manny.brawl.kit.type;

import gg.manny.spigot.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;

@AllArgsConstructor
public enum RankType {

    NONE(CC.WHITE),
    BASIC(CC.YELLOW),
    VIP(CC.GREEN),
    PRO(CC.GOLD),
    ELITE(CC.BLUE),
    EPIC(CC.DARK_PURPLE),
    LEGEND(CC.LIGHT_PURPLE),
    CRAFTER(CC.DARK_PURPLE),
    CRAFTER_PLUS(CC.DARK_RED);

    private final String colour;

    public String getDisplayName() {
        return this.colour + WordUtils.capitalizeFully(this.name().toLowerCase());
    }
}
