package rip.thecraft.brawl.ability.abilities;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.AbilityKillHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.SchedulerUtil;

@AbilityData
public class HealthBooster extends Ability implements AbilityKillHandler  {

    @AbilityProperty(id = "max-health")
    public double maxHealth = 40;

    @Override
    public void onApply(Player player) {
        SchedulerUtil.runTask(() -> {
            player.setMaxHealth(24);
            player.setHealth(player.getMaxHealth());
        }, false);
    }

    @Override
    public void onRemove(Player player) {
        SchedulerUtil.runTask(() -> {
            if (player != null) {
                player.setMaxHealth(20);
                if (!player.isDead()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
        }, false);
    }

    @Override
    public void onKill(Player killer, Player victim) {
        double newHealth = killer.getMaxHealth() + 4;
        if (newHealth <= maxHealth) {
            killer.setMaxHealth(newHealth);
        }
    }
}
