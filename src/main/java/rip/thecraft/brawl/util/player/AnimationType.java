package rip.thecraft.brawl.util.player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AnimationType {

    SWING(0),
    DAMAGE(1),

    CRITICALS_PARTICLE(4),
    ENCHANTMENT(5),

//    EAT(6),


    ;

    public int id;

}
