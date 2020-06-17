package rip.thecraft.brawl.ability.type;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.Human;
import rip.thecraft.spartan.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Illusioner extends Ability implements Listener{

    public static List<Human> registeredHumans = new ArrayList<>();

    @Override
    public Material getType() {
        return Material.SKULL_ITEM;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);


        for (int i = 0; i < 5; i++) {
            Human human = new Human(player.getWorld(), player.getName(), EntityUtils.getFakeEntityId(), player.getLocation().clone().add(i + 1, 0, i + 1), 0, player.getSkin());
            registeredHumans.add(human);
            new BukkitRunnable() {


                @Override
                public void run() {
                    human.setSprinting(player.isSprinting());
                    human.setCrouched(player.isSneaking());

                    human.setInventory(player.getItemInHand(), player.getInventory().getArmorContents());

                    human.setPitch(player.getLocation().getPitch());
                    human.setYaw(player.getLocation().getYaw());
                    Location to = player.getLocation();
                    Location from = human.getL();
                    human.walk(Math.abs(to.getX() - from.getX()) * 32, 0, Math.abs(to.getZ() - from.getZ()) * 32);
                }
            }.runTaskTimer(Brawl.getInstance(), 1L, 1L);
        }
    }

//    @Override
//    public void onPlayerMove(Player player, Location to, Location from) {
//        registeredHumans.forEach(human -> {
//            human.walk(Math.abs(to.getX() - from.getX()), 0, Math.abs(to.getZ() - from.getZ()));
//        });
//    }

    @EventHandler
    public void on(PlayerAnimationEvent event) {
        registeredHumans.forEach(Human::swingArm);
    }


}
