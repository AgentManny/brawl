package rip.thecraft.brawl.ability.task;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;

public class AbilityCooldownTask extends AbilityTask{

    protected AbilityCooldownTask(Ability ability, Player player, long duration, long ticks) {
        super(ability, player, duration, ticks);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onCancel() {

    }
}
