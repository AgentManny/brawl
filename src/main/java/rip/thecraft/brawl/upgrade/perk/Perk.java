package rip.thecraft.brawl.upgrade.perk;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Perk {

    BULLDOZER(
            "Killing a player grants you Strength I for 5 seconds.",
            10000
    );

    private String description;
    private int credits;


}
