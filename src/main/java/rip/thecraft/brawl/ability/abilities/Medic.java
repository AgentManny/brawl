package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.player.protection.Protection;

@AbilityData(
        name = "Medic",
        color = ChatColor.RED,
        icon = Material.POTION,
        data = (byte) 16421,
        displayIcon = false
)
// TODO REWORK MEDIC
public class Medic extends Ability implements Listener {

    private double health = 4;

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (hasEquipped(player)) {
            if (hasCooldown(player, false)) return;
            if (event.getRightClicked() instanceof LivingEntity && Protection.isAlly(player, (LivingEntity) event.getRightClicked())) {
                if (heal(player, (Player)event.getRightClicked())) {
                    return;
                }
            }
            addCooldown(player);
            heal(player, player);
        }
    }

    private boolean heal(Player player, Player target) {
        if (hasCooldown(player, false) || player.getHealth() == player.getMaxHealth()) {
            return false;
        }

        double bonus = this.health;
        double newHealth = Math.min(target.getMaxHealth(), bonus + target.getHealth());
        target.setHealth(newHealth);
        addCooldown(player);

        player.sendMessage(ChatColor.GREEN + "You've healed " + ChatColor.WHITE + (target == player ? "yourself" : target.getName()) + " " + ChatColor.GREEN + Double.toHexString(bonus) + " health.");
        return true;
    }
}
