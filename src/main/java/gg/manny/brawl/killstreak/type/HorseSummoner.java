package gg.manny.brawl.killstreak.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HorseSummoner extends Killstreak {

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

        horse.setCustomName(player.getDisplayName() + ChatColor.WHITE + (player.getDisguisedName().endsWith("s") ? "'" : "'s") + " Horse");
        horse.setCustomNameVisible(true);

        horse.setMaxHealth(40);
        horse.setHealth(horse.getMaxHealth());

        horse.setOwner(player);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Brawl.RANDOM.nextBoolean() ? Material.GOLD_BARDING : Material.DIAMOND_BARDING));

        horse.setPassenger(player);
    }
}
