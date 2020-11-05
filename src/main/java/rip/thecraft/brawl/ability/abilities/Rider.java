package rip.thecraft.brawl.ability.abilities;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.server.util.chatcolor.CC;

@RequiredArgsConstructor
public class Rider extends Ability implements Listener {

    private final Brawl plugin;

    private int duration = 10; // Seconds

    @Override
    public Material getType() {
        return Material.DIAMOND_BARDING;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.HORSE);
        horse.setAdult();

        horse.setCustomName(player.getDisplayName() + ChatColor.WHITE + "'s Horse");
        horse.setCustomNameVisible(true);

        horse.setMaxHealth(40);
        horse.setHealth(horse.getMaxHealth());

        horse.setOwner(player);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Brawl.RANDOM.nextBoolean() ? Material.GOLD_BARDING : Material.DIAMOND_BARDING));

        horse.setPassenger(player);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (player == null) {
                    cancel();
                    return;
                }

                if (horse != null || !horse.isDead()) {
                    horse.remove();
                }
            }

        }.runTaskLater(plugin, duration * 20L);
    }

    @Override
    public void onKill(Player player) {
        if (player.getVehicle() != null && player.getVehicle() instanceof Horse) {
            Horse entity = (Horse) player.getVehicle();
            entity.setMaxHealth(40);
            entity.setHealth(entity.getMaxHealth());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 4));
    }

    @EventHandler
    public void onIteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Horse) {
            Horse horse = (Horse) event.getRightClicked();
            if (horse.getOwner() != null) {
                if (!player.getUniqueId().equals(horse.getOwner().getUniqueId())) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RED + "That horse belongs to " + CC.GOLD + horse.getOwner().getName() + CC.RED + ".");
                }
            }
        }

    }

}