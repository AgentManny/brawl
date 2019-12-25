package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class NinjaStars extends Ability {


    @Override
    public void onInteractItem(Player player, Action action, ItemStack itemStack) {
        if (itemStack.getType() == Material.NETHER_STAR) {
            if (this.hasCooldown(player, true)) return;
            this.addCooldown(player, 1);

            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.getInventory().remove(player.getItemInHand());
            }

            Item item = player.getWorld().dropItem(player.getEyeLocation(),
                    new ItemBuilder(Material.NETHER_STAR)
                    .name("Ninja Star" + ThreadLocalRandom.current().nextInt(1, 1000))
                    .create()
            );
            item.setPickupDelay(Integer.MAX_VALUE);
            item.setVelocity(player.getEyeLocation().getDirection().multiply(1.4));


            // detect if it be hitting someone since there isnt a collision check for dropped items and i dont want it riding a snowball
            new BukkitRunnable() {

                long timestamp = System.currentTimeMillis();
                Player hit;

                @Override
                public void run() {
                    if ((System.currentTimeMillis() - timestamp) > 750 || item == null) {
                        cancel();
                        return;
                    }

                    if (item.isDead()) {
                        cancel();
                        return;
                    }

                    item.getNearbyEntities(1, 2.5, 1).stream().filter(other -> other instanceof Player && !player.equals(other)).findAny().ifPresent(player2 -> {
                        hit = (Player) player2;
                        if (hit != null && !hit.isDead()) {
                            double damageHealth = 0.5; // 0.5 hearts
                            if (hit.getHealth() - damageHealth > 0) {
                                hit.setHealth(hit.getHealth() - damageHealth);
                                addCooldown(player, 5);
                            }
                            hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 250, 1));
                            hit.damage(damageHealth, player); // make it so it counts as a player kill

                        }
                        cancel();
                    });

                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    item.remove();

                    super.cancel();
                }
            }.runTaskTimer(Brawl.getInstance(), 4L, 4L);

        }
    }


    @Override
    public void onKill(Player player) {
        ItemStack fireball = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.isSimilar(new ItemStack(Material.NETHER_STAR))) {
                fireball = item;
                item.setAmount(item.getAmount() + 5);
            }
        }

        if (fireball == null) {
            player.getInventory().setItem(1, new ItemStack(Material.NETHER_STAR, 5));
        }
        player.updateInventory();
    }

}