package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.BrawlUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Dash extends Ability {

    private double damage = 3.5;
    private double speed = 2;

    @Override
    public Material getType() {
        return Material.SUGAR;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        Vector vector = player.getLocation().getDirection();
        vector.setY(0);
        vector.multiply(speed / vector.length());
        vector.setY(0.3);
        player.setVelocity(vector);

        new BukkitRunnable() {

            long start = System.currentTimeMillis();

            @Override
            public void run() {
                if (player == null || (System.currentTimeMillis() - start > 2000L)) {
                    cancel();
                    return;
                }

                BrawlUtil.getNearbyPlayers(player, 2.25).forEach(target -> target.damage(damage, player));
            }

        }.runTaskTimer(Brawl.getInstance(), 4L, 4L);
    }

    @Override
    public void onKill(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
    }
}
