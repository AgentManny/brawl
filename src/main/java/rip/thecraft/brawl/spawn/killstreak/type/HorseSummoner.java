package rip.thecraft.brawl.spawn.killstreak.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.killstreak.Killstreak;
import rip.thecraft.brawl.player.PlayerData;

public class HorseSummoner extends Killstreak implements Listener {

    @Override
    public int[] getKills() {
        return new int[] { 20 };
    }

    @Override
    public String getName() {
        return "Horse Summoner";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public Material getType() {
        return Material.DIAMOND_BARDING;
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public void onActivate(Player player, PlayerData playerData) {
        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.HORSE);
        horse.setAdult();
        String name = ChatColor.WHITE + player.getName() + "'s Horse";
        horse.setCustomName(name.substring(0, Math.min(name.length(), 24)));
        horse.setCustomNameVisible(true);

        horse.setMaxHealth(40);
        horse.setHealth(horse.getMaxHealth());

        horse.setOwner(player);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Brawl.RANDOM.nextBoolean() ? Material.GOLD_BARDING : Material.DIAMOND_BARDING));

        horse.setPassenger(player);
    }

    @EventHandler
    public void onVehicleEXIT(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Horse) {
            vehicle.remove();
        }
    }
}
