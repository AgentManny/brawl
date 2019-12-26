package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.player.protection.Protection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Assassin extends Ability implements Listener {

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
        Brawl.getInstance().getServer().getOnlinePlayers().forEach(online  -> {
            if (!Protection.isAlly(online, player)) {
                Brawl.getInstance().getEntityHider().hideEntity(online, player);
            }
        });
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 150, 1));

        int taskId = new BukkitRunnable() {

            private ItemStack[] armorCopy = player.getInventory().getArmorContents().clone();

            @Override
            public void run() {
                if (player != null) {
                    if (hasEquipped(player)) {
                        player.getInventory().setArmorContents(armorCopy);
                        player.removeMetadata("assassin_id", Brawl.getInstance());
                        player.sendMessage(ChatColor.GREEN + "You've returned to your normal state.");
                    }
                    Brawl.getInstance().getServer().getOnlinePlayers().forEach(online  -> Brawl.getInstance().getEntityHider().showEntity(online, player));
                }

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
            }
        }.runTaskLater(Brawl.getInstance(), 200L).getTaskId();
        player.getInventory().setArmorContents(null);
        player.setMetadata("assassin_id", new FixedMetadataValue(Brawl.getInstance(), taskId));
    }


}
