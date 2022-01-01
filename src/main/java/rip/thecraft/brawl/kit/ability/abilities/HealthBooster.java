package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.handlers.KillHandler;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.SchedulerUtil;

@AbilityData(
        name = "Health Booster",
        color = ChatColor.RED,
        icon = Material.REDSTONE,
        displayIcon = false
)
public class HealthBooster extends Ability implements KillHandler {

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
