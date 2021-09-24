package rip.thecraft.brawl.ability.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityTask;
import rip.thecraft.brawl.util.ParticleEffect;

public class Dragon extends Ability implements Listener {

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getType() {
        return Material.FIREBALL;
    }

    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        player.sendMessage("you clicked item");
        new DragonTask(player).start();
    }

    public class DragonTask extends AbilityTask {

        double t = 0, o = 0;
        Vector direction;
        Location location;

        protected DragonTask(Player player) {
            super(player, 5000L, 1L);

            direction = player.getLocation().clone().getDirection().normalize();
            location = player.getLocation().clone();
        }

        @Override
        public void onTick() {
            t += 0.5;
            o += 0.7;
            double x = direction.getX() * t + Math.cos(o);
            double y = direction.getY() * t + 1.5 + Math.sin(o);
            double z = direction.getZ() * t + Math.sin(o);

            location.add(x, y, z);
            ParticleEffect.FLAME.display(0, 0, 0, 0, 1, location, 50);
            location.subtract(x, y, z);
        }

        @Override
        public void onCancel() { }
    }


}
