package rip.thecraft.brawl.kit.type;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import rip.thecraft.server.util.chatcolor.CC;

@AllArgsConstructor
public enum RankType {

    NONE(CC.GRAY),
    SILVER(CC.WHITE), // $10
    GOLD(CC.GOLD), // // $20
    PLATINUM(CC.BLUE), // $35
    DIAMOND(CC.AQUA), // $50
    MASTER(CC.LIGHT_PURPLE), // $75
    CHAMPION(CC.DARK_PURPLE); // $100

    private final String colour;

    public String getDisplayName() {
        return this.colour + WordUtils.capitalizeFully(this.name().toLowerCase());
    }

    public String getName() {
        return WordUtils.capitalizeFully(this.name().toLowerCase());
    }

}
