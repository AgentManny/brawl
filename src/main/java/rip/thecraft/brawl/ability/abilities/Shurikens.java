package rip.thecraft.brawl.ability.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Shurikens extends Ability {

    @Override
    public boolean bypassAbilityPreventZone() {
        return true;
    }

    @Override
    public boolean onInteractItem(Player player, Action action, ItemStack itemStack) {
        if (itemStack.getType() == Material.NETHER_STAR) {
            if (this.hasCooldown(player, true)) return true;
            this.addCooldown(player, TimeUnit.SECONDS.toMillis(10));

            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.getInventory().remove(player.getItemInHand());
            }

            Item item = player.getWorld().dropItem(player.getEyeLocation(),
                    new ItemBuilder(Material.NETHER_STAR)
                    .name("Shurikens" + ThreadLocalRandom.current().nextInt(1, 1000))
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
                    if ((System.currentTimeMillis() - timestamp) > 1500L || item == null) {
                        cancel();
                        return;
                    }

                    if (item.isDead()) {
                        cancel();
                        return;
                    }

                    item.getNearbyEntities(1, 3, 1).stream().filter(other -> other instanceof Player && !player.equals(other)).findAny().ifPresent(player2 -> {
                        hit = (Player) player2;
                        if (!hit.isDead()) {
                            double damageHealth = 3; // 0.5 hearts
                            if (hit.getHealth() - damageHealth > 0) {
                                hit.setHealth(hit.getHealth() - damageHealth);
                                addCooldown(player, 5);
                            }
                            hit.damage(damageHealth, player); // make it so it counts as a player kill
                            hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, true, false));
                            cancel();
                            return;
                        }
                    });

                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    item.remove();

                    super.cancel();
                }
            }.runTaskTimer(Brawl.getInstance(), 4L, 4L);

            return true;
        }

        return false;
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
