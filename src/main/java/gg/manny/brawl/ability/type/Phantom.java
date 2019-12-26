package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Phantom extends Ability {

    private final Brawl plugin = Brawl.getInstance();

    @Override
    public Material getType() {
        return Material.FEATHER;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GRAY;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }.runTaskLater(plugin, MathUtil.convertSecondstoTicks(MathUtil.getRandomInt(5, 10)));
    }
}
