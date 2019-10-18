package gg.manny.brawl.duelarena.duel;

import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.server.knockback.profile.KnockbackProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class DuelBuilder {

    private final Player target;
    private final MatchLoadout loadout;

    private KnockbackProfile knockback;

    private int matchAmounts;

    private int refillAmount;


}
