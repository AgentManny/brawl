package rip.thecraft.brawl.perks;

import lombok.Getter;

/**
 * Created by Flatfile on 12/21/2021.
 */
@Getter
public enum PerkSlot {

    ONE(1),
    TWO(2),
    THREE(3);

    private int value;

    PerkSlot(int value) {
        this.value = value;
    }
}
