package gg.manny.brawl.kit.type;

import gg.manny.spigot.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;

@AllArgsConstructor
public enum RarityType {

    NONE(CC.WHITE),
    COMMON(CC.GRAY),
    UNCOMMON(CC.GREEN),
    RARE(CC.BLUE),
    EPIC(CC.DARK_PURPLE),
    LEGENDARY(CC.GOLD);

    @Getter
    private String colour;

    public String getDisplayName() {
        return this.colour + WordUtils.capitalizeFully(this.name().toLowerCase());
    }

}
