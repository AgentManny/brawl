package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;

public class Assassin extends Ability implements Listener {

    private static String STEALTH_METADATA = "Stealth";

    @Override
    public Material getType() {
        return Material.SULPHUR;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);


        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 150, 1));

        player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now in your stealth state!");
        player.sendMessage(ChatColor.GRAY + "You are hidden from normal players and your damage has increased.");

        int taskId = new BukkitRunnable() {

            private ItemStack[] armorCopy = player.getInventory().getArmorContents().clone();

            @Override
            public void run() {
                if (player != null && hasEquipped(player)) {
                    player.getInventory().setArmorContents(armorCopy);
                    player.removeMetadata(STEALTH_METADATA, Brawl.getInstance());
                    player.sendMessage(ChatColor.GREEN + "You've returned to your normal state.");
                }
            }

        }.runTaskLater(Brawl.getInstance(), 200L).getTaskId();

        player.getInventory().setArmorContents(null);
        player.setMetadata(STEALTH_METADATA, new FixedMetadataValue(Brawl.getInstance(), taskId));
    }

    @Override
    public void onDeactivate(Player player) {
        if (player.hasMetadata(STEALTH_METADATA)) {
            int taskId = player.getMetadata(STEALTH_METADATA, Brawl.getInstance()).asInt();
            if (Brawl.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId)) {
                Brawl.getInstance().getServer().getScheduler().cancelTask(taskId);
            }
        }
    }
}