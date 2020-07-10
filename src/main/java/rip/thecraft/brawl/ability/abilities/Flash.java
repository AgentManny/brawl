package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flash extends Ability {

    private static final Set<Material> invalidBlocks = new HashSet<>();
    private static String invalidTeleport = ChatColor.RED + "You can't teleport to this location!";

    public boolean giveWeakness = true;
    public int maxTeleportDistance = 100;

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
        if (blocks.size() > 1 && blocks.get(1).getType() != Material.AIR) {

            this.addCooldown(player);

        } else {
            player.sendMessage();
        }


    }
}
