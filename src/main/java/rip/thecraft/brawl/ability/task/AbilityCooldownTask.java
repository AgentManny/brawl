package rip.thecraft.brawl.ability.task;

import org.bukkit.entity.Player;

public class AbilityCooldownTask extends AbilityTask{

    protected AbilityCooldownTask(Player player, long duration, long ticks) {
        super(player, duration, ticks);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onCancel() {

    }
}
