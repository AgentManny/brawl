package rip.thecraft.brawl.region.zone;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ZoneType {

    SPAWN(ChatColor.GOLD);


    private String name;
    private ChatColor color;

    ZoneType(ChatColor color) {
        this.name = WordUtils.capitalizeFully(name().toLowerCase().replace("_", " "));
        this.color = color;
    }

}
