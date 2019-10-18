package gg.manny.brawl.killstreak.type;

import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.player.PlayerData;
import net.minecraft.server.v1_7_R4.EntityWolf;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftWolf;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AttackDogs extends Killstreak {

    @Override
    public int[] getKills() {
        return new int[] { 25 };
    }

    @Override
    public String getName() {
        return "Attack Dogs";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.WHITE;
    }

    @Override
    public void onKill(Player player, PlayerData playerData) {
        for (int i = 0; i < 5; i++) {
            Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);

            wolf.setOwner(player);
            wolf.setTamed(true);
            wolf.setAgeLock(true);
            wolf.setAdult();

            wolf.setMaxHealth(50);
            wolf.setHealth(wolf.getMaxHealth());

            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            EntityWolf nmsWolf = ((CraftWolf)wolf).getHandle();
        }
    }
}
