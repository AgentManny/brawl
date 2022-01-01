package rip.thecraft.brawl.spawn.market.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.player.PlayerData;

public class KitPassButton extends MarketItem {

    public KitPassButton() {
        super("Kit Pass", Material.PAPER, 500);
        setConfirm(false);
    }

    @Override
    public int getWeight() {
        return 7;
    }

    @Override
    public String getDescription() {
        return "Kit Pass to use a kit for 30 minutes.";
    }

    @Override
    public boolean getRequiresSpawn() {
        return false;
    }

    @Override
    public void purchase(Player player, PlayerData playerData) {
        playerData.setKitPasses(playerData.getKitPasses() + 1);
//        player.sendMessage(ChatColor.GREEN + " + " + ChatColor.BOLD + 1 + ChatColor.GREEN + " Kit Pass");

    }
}