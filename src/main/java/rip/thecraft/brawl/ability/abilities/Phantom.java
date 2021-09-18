package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.MathUtil;

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
        if (hasCooldown(player, true)) return;
        addCooldown(player);

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

    @Override
    public void onDeactivate(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);
    }
}
