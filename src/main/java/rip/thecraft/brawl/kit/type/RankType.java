package rip.thecraft.brawl.kit.type;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import rip.thecraft.server.util.chatcolor.CC;

@AllArgsConstructor
public enum RankType {

    NONE(CC.GRAY),
    BASIC(CC.YELLOW),
    VIP(CC.GREEN), // 10
    PRO(CC.GOLD), // 25
    ELITE(CC.AQUA), // 35
    EPIC(CC.BLUE), // 50
    LEGEND(CC.LIGHT_PURPLE), // 75
    CRAFTER(CC.DARK_PURPLE); // 100

    private final String colour;

    public String getDisplayName() {
        return this.colour + WordUtils.capitalizeFully(this.name().toLowerCase());
    }

    public String getName() {
        return WordUtils.capitalizeFully(this.name().toLowerCase());
    }

}
