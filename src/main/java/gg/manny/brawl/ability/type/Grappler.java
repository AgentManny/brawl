package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import net.minecraft.server.v1_7_R4.EntityFishingHook;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class Grappler extends Ability implements Listener {

    private double hookThreshold = 0.25;
    private double hForceMult = 0.3;
    private double hForceMax = 5;
    private double vForceMult = 0.25;
    private double vForceBonus = 0.5;
    private double vForceMax = 1.5;

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;

        if (this.hasEquipped(player)) {
            if (this.hasCooldown(player, true)) return;
            if (event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
                Entity entityHook = event.getHook();
                Block block = entityHook.getWorld().getBlockAt(entityHook.getLocation().add(0, -hookThreshold, 0));
                if (!block.isEmpty() && !block.isLiquid()) {
                    Vector vectorDistance = entityHook.getLocation().subtract(player.getLocation()).toVector();
                    if (vectorDistance.getY() < 0.0) {
                        vectorDistance.setY(0.0);
                    }

                    vectorDistance
                            .setX(vectorDistance.getX() * hForceMult)
                            .setY(vectorDistance.getY() * vForceMult + vForceBonus)
                            .setZ(vectorDistance.getZ() * hForceMult);

                    double distance = hForceMax * hForceMax;
                    if (vectorDistance.clone().setY(0.0).lengthSquared() > distance) {
                        distance = distance / vectorDistance.lengthSquared();
                        vectorDistance.setX(vectorDistance.getX() * distance);
                        vectorDistance.setZ(vectorDistance.getZ() * distance);
                    }

                    if (vectorDistance.getY() > vForceMax) {
                        vectorDistance.setY(vForceMax);
                    }
                    this.addCooldown(player, 15);
                    player.setVelocity(vectorDistance);
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
