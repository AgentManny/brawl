package rip.thecraft.brawl.ability.abilities.legacy;

import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.player.protection.Protection;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Medic extends Ability implements Listener {

    private double health = 4;

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (this.hasEquipped(player)) {
            if (this.hasCooldown(player, false)) return;
            if (event.getRightClicked() instanceof LivingEntity && Protection.isAlly(player, (LivingEntity) event.getRightClicked())) {
                if (heal(player, (Player)event.getRightClicked())) {
                    return;
                }
            }
            this.addCooldown(player);
            this.heal(player, player);
        }
    }

    private boolean heal(final Player player, final Player target) {
        if (this.hasCooldown(player, false) || player.getHealth() == player.getMaxHealth()) {
            return false;
        }

        final double bonus = this.health;
        final double newHealth = Math.min(target.getMaxHealth(), bonus + target.getHealth());
        target.setHealth(newHealth);
        this.addCooldown(player);

        player.sendMessage(ChatColor.GREEN + "You've healed " + ChatColor.WHITE + (target == player ? "yourself" : target.getName()) + " " + ChatColor.GREEN + Double.toHexString(bonus) + " health.");
        return true;
    }
}
