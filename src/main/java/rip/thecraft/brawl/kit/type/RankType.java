package rip.thecraft.brawl.kit.type;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import rip.thecraft.server.util.chatcolor.CC;

@AllArgsConstructor
public enum RankType {

    NONE(CC.WHITE),
    BASIC(CC.YELLOW),
    VIP(CC.GREEN),
    PRO(CC.GOLD),
    ELITE(CC.AQUA),
    EPIC(CC.BLUE),
    LEGEND(CC.LIGHT_PURPLE),
    CRAFTER(CC.DARK_PURPLE);

    private final String colour;

    public String getDisplayName() {
        return this.colour + WordUtils.capitalizeFully(this.name().toLowerCase());
    }

    public String getName() {
        return WordUtils.capitalizeFully(this.name().toLowerCase());
    }

}
