package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.util.ArmorUtil;
import rip.thecraft.brawl.util.BrawlUtil;

import java.util.concurrent.ThreadLocalRandom;

@AbilityData(icon = Material.REDSTONE_TORCH_ON, color = ChatColor.RED)
public class Detonator extends Ability {

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        if (ArmorUtil.contains(ArmorUtil.LEATHER, player)) {
            new BukkitRunnable() {

                boolean red = true; // Default to be true
                int count = 0;
                int finishedCount = ThreadLocalRandom.current().nextInt(4, 24);


                @Override
                public void run() {
                    if (player == null) {
                        cancel();
                        return;
                    }

                    for (ItemStack item : player.getInventory().getArmorContents()) {
                        if (item.getItemMeta() instanceof LeatherArmorMeta) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                            meta.setColor(red ? Color.RED : Color.WHITE);
                            item.setItemMeta(meta);
                        }
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.NOTE_STICKS, 1.4f, 1.5f);
                    red = !red;
                    if (count++ > finishedCount) {
                        cancel();
                    }
                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();

                    if (player != null) {
                        for (ItemStack item : player.getInventory().getArmorContents()) {
                            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                                meta.setColor(Color.RED);
                                item.setItemMeta(meta);
                            }
                        }

                        explode(player);
                    }
                }
            }.runTaskTimer(Brawl.getInstance(), 4L, 4L);
        } else {
            explode(player);
        }
    }

    private void explode(Player player) {
        for (Player nearby : BrawlUtil.getNearbyPlayers(player, 5)) {
            nearby.damage(1.25, player);
        }

        double chance = Math.random() * 100;
        Location location = player.getLocation().add(0.0D, 1.0D, 0.0D);

        player.setVelocity(player.getLocation().getDirection().clone().normalize().multiply(2.5));
        player.sendMessage(ChatColor.YELLOW + "You were boosted away from the explosion!");

        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {

            for (int i = 0; i < 3; i++) {
                TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
                tnt.setFuseTicks(ThreadLocalRandom.current().nextInt(2, 5) + i);
                tnt.setMetadata("tnt", new FixedMetadataValue(Brawl.getInstance(), Boolean.TRUE));
            }

            if (player != null) {

                player.setHealth(2);
                player.damage(0, player);

            }

        }, 15L);
    }
}
