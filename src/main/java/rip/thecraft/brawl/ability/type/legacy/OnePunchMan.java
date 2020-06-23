package rip.thecraft.brawl.ability.type.legacy;

import rip.thecraft.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class OnePunchMan extends Ability implements Listener {

    @Override
    public Material getType() {
        return Material.ICE;
    }

    @Override
    public String getName() {
        return "Freeze Player";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled() && !(event.getDamager() instanceof Player)) return;


        Player victim = (Player) event.getEntity();
        Player player = (Player) event.getDamager();

        if (this.hasEquipped(player)) return;

        ItemStack holdingItem = player.getItemInHand();
        if (holdingItem.equals(getIcon()));
//
//
//        Player shooter = null;
//        if (event.getDamager() instanceof Projectile) {
//            Projectile projectile = (Projectile) event.getDamager();
//            if (projectile.getShooter() instanceof Player) {
//                shooter = (Player) projectile.getShooter();
//            }
//        }
//        if (shooter != null) {
//            Kit selectedKit = KitHandler.getEquipped(shooter);
//            if (selectedKit != null) {
//                for (Ability ability : selectedKit.getAbilities()) {
//                    if (ability.onProjectileHit(shooter, (Player) event.getEntity(), event)) {
//                        event.setCancelled(true);
//                        break;
//                    }
//                }
//            }
//        }
    }


}
