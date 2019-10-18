package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import net.minecraft.server.v1_7_R4.EntityFishingHook;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class Grappler extends Ability implements Listener {

    private double range = 15;
    private double speed = 0.425;

    @Override
    public Material getType() {
        return Material.FISHING_ROD;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if(this.hasEquipped(player)) {
            if (event.getCaught() instanceof Player) {
                if (this.hasCooldown(player, true)) return;
                this.addCooldown(player);

                Player hook = (Player) event.getCaught();
                if (hook.getLocation().subtract(player.getLocation()).length() <= this.range) {
                    Vector speed = hook.getLocation().subtract(player.getLocation()).toVector().multiply(this.speed);
                    speed.setY(speed.getY() / 2);
                    speed.setY(speed.getY() + 0.3);
                    player.setVelocity(speed);
                }
            }
        }
    }

    public Entity spawnFish(Location location, EntityHuman entityhuman) {
        WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
        net.minecraft.server.v1_7_R4.Entity hook = new EntityFishingHook(world, entityhuman);
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), null, (org.bukkit.entity.Fish) hook.getBukkitEntity(), PlayerFishEvent.State.FISHING); // for Bukkit's event handling
        world.getServer().getPluginManager().callEvent(playerFishEvent); // for Bukkit's event handling
        world.makeSound(entityhuman, "random.bow", 0.5F, 0.2F); // you can remove/play with this
        world.addEntity(hook);
        entityhuman.aZ(); // Not sure if this is necessary, feel free to play around with it
        return hook.getBukkitEntity();
    }
}
