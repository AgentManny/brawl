package rip.thecraft.brawl.ability.abilities.legacy;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BlizzardBlaster extends Ability {

    private int radius = 5;
    private int time = 15;

    @Override
    public String getName() {
        return "Blizzard Blaster";
    }

    @Override
    public Material getType() {
        return Material.SNOW;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to activate this ability.");
            return;
        }

        this.addCooldown(player);


        BukkitTask task = new BukkitRunnable() {

            Vector vector = player.getLocation().getDirection().normalize().multiply(0.3)
                    .setY(0);

            Location location = player.getLocation()
                    .subtract(0, 1, 0)
                    .add(vector);

            List<Entity> visualEntites = new ArrayList<>();

            @Override
            public void run() {
                if (location.getBlock().getType() != Material.AIR && location.getBlock().getType().isSolid()) {
                    location.add(0, 1, 0);
                }

                if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                    if (!location.clone().getBlock().getType().toString().contains("SLAB")) location.add(0, -1, 0);
                }

                for (int i = 0; i < 3; i++) {
                }

                location.add(vector);
            }


            @Override
            public synchronized void cancel() throws IllegalStateException {

            }

        }.runTaskTimer(Brawl.getInstance(), 4L, 4L);

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            if (task != null) {
                task.cancel();
            }
        }, 40L);
    }

}
