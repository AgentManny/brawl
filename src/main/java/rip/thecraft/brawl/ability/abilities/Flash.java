package rip.thecraft.brawl.ability.abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.BlockUtil;

import java.util.HashSet;
import java.util.List;

public class Flash extends Ability {

    private static final HashSet<Byte> invalidBlocks = new HashSet<>();

    private boolean giveWeakness = false;
    private int maxTeleportDistance = 50;

    static {
        invalidBlocks.add((byte) Material.BARRIER.getId());
        invalidBlocks.add((byte) Material.SNOW.getId());
        invalidBlocks.add((byte) Material.LONG_GRASS.getId());
        invalidBlocks.add((byte) Material.RED_MUSHROOM.getId());
        invalidBlocks.add((byte) Material.RED_ROSE.getId());
        invalidBlocks.add((byte) Material.YELLOW_FLOWER.getId());
        invalidBlocks.add((byte) Material.BROWN_MUSHROOM.getId());
        invalidBlocks.add((byte) Material.SIGN_POST.getId());
        invalidBlocks.add((byte) Material.WALL_SIGN.getId());
        invalidBlocks.add((byte) Material.FIRE.getId());
        invalidBlocks.add((byte) Material.TORCH.getId());
        invalidBlocks.add((byte) Material.REDSTONE_WIRE.getId());
        invalidBlocks.add((byte) Material.REDSTONE_TORCH_OFF.getId());
        invalidBlocks.add((byte) Material.REDSTONE_TORCH_ON.getId());
        invalidBlocks.add((byte) Material.VINE.getId());
        invalidBlocks.add((byte) Material.WATER_LILY.getId());
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

        Location blockLoc;
        List<Block> blocks = player.getLastTwoTargetBlocks((HashSet<Byte>)null, maxTeleportDistance);
        if (blocks.size() > 1 && blocks.get(1).getType() == Material.AIR) {
            Location maxLocation = player.getLocation().add(player.getLocation().getDirection().multiply(maxTeleportDistance));
            blockLoc = BlockUtil.isOnGround(maxLocation, 1) ? maxLocation : player.getWorld().getHighestBlockAt(maxLocation).getLocation();
        } else {
            blockLoc = blocks.get(0).getLocation();
        }

        Location playerLoc = player.getLocation();
        double distance = playerLoc.distance(blockLoc);
        if (distance > 2) {
            Location loc = blockLoc.add(0.5, 0.5, 0.5);
            loc.setPitch(playerLoc.getPitch());
            loc.setYaw(playerLoc.getYaw());

            if (loc.getBlockY() >= 150 || RegionType.SAFEZONE.appliesTo(loc)) {
                player.sendMessage(ChatColor.RED + "You can't teleport here!");
                return;
            }

            player.eject();
            player.teleport(loc);

            playerLoc.getWorld().playSound(playerLoc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
            playerLoc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);

            playerLoc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
            loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);

            if (giveWeakness) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) ((distance / 2) * 20), 1), true);
            }

            if (player.getFallDistance() > 10) {
                // half the damage if teleporting down from high places
                player.setFallDistance(player.getFallDistance() / 2);
            }
            addCooldown(player);
        } else {
            player.sendMessage(ChatColor.RED + "You can't teleport this close!");
        }
    }
}
