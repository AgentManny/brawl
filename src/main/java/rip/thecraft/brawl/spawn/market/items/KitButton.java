package rip.thecraft.brawl.spawn.market.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;

public class KitButton extends MarketItem {

    private final Kit kit;

    public KitButton(Kit kit, double storeMultiplier) {
        super("Rent " + kit.getName(), kit.getIcon().getType(), (int) (kit.getPrice() * storeMultiplier));

        this.kit = kit;
        setConfirm(true);
    }

    @Override
    public int getWeight() {
        return 9;
    }

    @Override
    public String getDescription() {
        return "Rent this kit for 24 hours";
    }

    @Override
    public void purchase(Player player, PlayerData playerData) {
        if (!playerData.hasKit(kit)) {
            playerData.getKitRentals().put(kit.getName(), System.currentTimeMillis() + (24 * 3600 * 1000));
        } else {
            player.sendMessage(ChatColor.RED + "You already own this kit.");
        }
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}