package rip.thecraft.brawl.player.task;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.util.Cooldown;

@AllArgsConstructor
public class PlayerCooldownTask extends BukkitRunnable {

    private Player player;
    private PlayerData playerData;

    private Ability ability;
    private ItemStack abilityIcon;

    private Cooldown cooldown;

    private int second;

    @Override
    public void run() {
        if (player == null || !player.isOnline() || cooldown == null || cooldown.hasExpired()) {
            cancel();
            return;
        }

        float percentLeft = (float) cooldown.getRemaining() / ability.getCooldown();
        player.setExp(percentLeft);

        int secondsLeft = (int) (cooldown.getRemaining() / 1_000);
        if (secondsLeft != second) {
            this.second = secondsLeft;
//            tick();
            player.setLevel(second);
        }
    }

    @Deprecated // Requires an update inventory every time I set it ;-; too intensive
    public void tick() {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == abilityIcon.getType() && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (abilityIcon.getItemMeta().getDisplayName().equals(displayName)) {
                    item.setType(Material.INK_SACK);
                    item.setDurability((byte) 8);
                    item.setAmount(second);
                    break;
                }
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        if (player != null && cooldown != null) {
            if (!cooldown.isNotified()) {
                player.sendMessage(ChatColor.GREEN + "You can now use " + ChatColor.BOLD + ability.getName() + ChatColor.GREEN + " again.");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1.2f);
                cooldown.setNotified(true);
                ability.onCooldownExpire(player);
            }

            if (!playerData.isSpawnProtection()) { // Should be displaying their Level at spawn instead
                player.setLevel(0);
                player.setExp(0);
            }
            playerData.setCooldownTask(null);
        }
    }
}
