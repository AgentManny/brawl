package rip.thecraft.brawl.ability.abilities;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FlameThrower extends Ability implements Listener {

    private int duration = 10;

    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);

    @Override
    public Material getType() {
        return Material.FIREWORK_CHARGE;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public void onApply(Player player) {
        player.addPotionEffect(potionEffect);
    }

    @Override
    public void onRemove(Player player) {
        player.getActivePotionEffects().removeIf(potionEffect -> this.potionEffect.getType().equals(potionEffect.getType()));
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        new BukkitRunnable() {

            final Random r = Brawl.RANDOM;
            int time = 0;

            @Override
            public void run() {
                Location location = player.getLocation();

                if (time++ > duration) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < 5; i ++) {
                    FallingBlock block = location.getWorld().spawnFallingBlock(location.clone().add(r.nextInt(2) - 1, r.nextInt(2) - 1, r.nextInt(2) - 1), Material.FIRE, (byte) 0);
                    block.setVelocity(location.getDirection().multiply(5));
                    block.setDropItem(false);
                    block.setFireTicks(20);
                }
            }

        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FIRE) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
}
