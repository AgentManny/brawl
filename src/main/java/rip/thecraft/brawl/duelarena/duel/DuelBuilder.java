package rip.thecraft.brawl.duelarena.duel;

import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class DuelBuilder {

    private final Player target;
    private final MatchLoadout loadout;

    //private KnockbackProfile knockback; - Temp removed

    private int matchAmounts;

    private int refillAmount;


}
