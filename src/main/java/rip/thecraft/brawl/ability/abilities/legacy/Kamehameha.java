package rip.thecraft.brawl.ability.abilities.legacy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.ability.Ability;

public class Kamehameha extends Ability implements Listener{

    @Override
    public Material getType() {
        return Material.FIREWORK_CHARGE;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);
//
//        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
//            public void run(){
//                for(Player t : Bukkit.getOnlinePlayers()){
//                    try {
//                        Vector velocity = t.getLocation().getDirection();
//                        Vector addition = velocity.clone().multiply(0.1);
//                        for(int i = 0; i < 100; i++){
//                            Location loc = velocity.toLocation(t.getWorld());
//
//                            ).RED_DUST.send(Bukkit.getOnlinePlayers(), loc.clone().add(t.getLocation()).add(0, 1D, 0), Color.BLUE);
//                            ParticleEffect.RED_DUST.send(Bukkit.getOnlinePlayers(), loc.clone().add(t.getLocation()).add(0, 1D, 0), Color.AQUA)
//                            velocity.add(addition);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, 0L, 1L);


    }

}
