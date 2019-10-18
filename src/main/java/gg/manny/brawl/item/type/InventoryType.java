package gg.manny.brawl.item.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryType {

    SPAWN("LANGUAGE.ITEM.SPAWN"),
    SPECTATOR("LANGUAGE.ITEM.SPECTATOR"),
    ARENA("LANGUAGE.ITEM.ARENA"),
    QUEUE("LANGUAGE.ITEM.QUEUE");

    private final String path;

}
