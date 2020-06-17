package rip.thecraft.brawl.task;

import rip.thecraft.brawl.Brawl;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class SoupTask extends BukkitRunnable {

    private final Brawl plugin;

    @Override
    public void run() {
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.HUGE_MUSHROOM_2 && player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
            }
        }
    }
}
