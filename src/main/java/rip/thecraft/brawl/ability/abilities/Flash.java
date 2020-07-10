package rip.thecraft.brawl.ability.abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flash extends Ability {

    private static final Set<Material> invalidBlocks = new HashSet<>();

    private boolean giveWeakness = true;
    private int maxTeleportDistance = 100;

    static {
        invalidBlocks.add(Material.BARRIER);
        invalidBlocks.add(Material.SNOW);
        invalidBlocks.add(Material.LONG_GRASS);
        invalidBlocks.add(Material.RED_MUSHROOM);
        invalidBlocks.add(Material.RED_ROSE);
        invalidBlocks.add(Material.YELLOW_FLOWER);
        invalidBlocks.add(Material.BROWN_MUSHROOM);
        invalidBlocks.add(Material.SIGN_POST);
        invalidBlocks.add(Material.WALL_SIGN);
        invalidBlocks.add(Material.FIRE);
        invalidBlocks.add(Material.TORCH);
        invalidBlocks.add(Material.REDSTONE_WIRE);
        invalidBlocks.add(Material.REDSTONE_TORCH_OFF);
        invalidBlocks.add(Material.REDSTONE_TORCH_ON);
        invalidBlocks.add(Material.VINE);
        invalidBlocks.add(Material.WATER_LILY);
    }

    @Override
    public Material getType() {
        return Material.REDSTONE_TORCH_ON;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.RED;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

        List<Block> blocks = player.getLastTwoTargetBlocks(invalidBlocks, maxTeleportDistance);
        if (blocks.size() > 1 && blocks.get(1).getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You can't teleport this far!");
            return;
        }

        Location playerLoc = player.getLocation();
        Location blockLoc = blocks.get(0).getLocation().clone();
        double distance = playerLoc.distance(blockLoc);
        if (distance > 2) {
            Location loc = blockLoc.add(0.5, 0.5, 0.5);
            loc.setPitch(playerLoc.getPitch());
            loc.setYaw(playerLoc.getYaw());

            player.eject();
            player.teleport(loc);

            playerLoc.getWorld().playSound(playerLoc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
            playerLoc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);

            playerLoc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);

            if (giveWeakness) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) ((distance / 2) * 20), 1), true);
            }

            addCooldown(player);
        } else {
            player.sendMessage(ChatColor.RED + "You can't teleport this close!");
        }
    }
}
