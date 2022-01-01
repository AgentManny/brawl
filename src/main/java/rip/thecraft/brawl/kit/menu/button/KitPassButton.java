package rip.thecraft.brawl.kit.menu.button;

import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;

import java.util.List;


public class KitPassButton extends Button {

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> lore = ItemBuilder.wrap("You have " + ChatColor.WHITE + playerData.getKitPasses() + ChatColor.GRAY + " kit pass. Right click a kit to activate a kit pass for " + ChatColor.GREEN + "30 minutes" + ChatColor.GRAY + ".", ChatColor.GRAY.toString(), 30, false);
        return new ItemBuilder(Material.PAPER)
                .name(CC.GREEN + ChatColor.BOLD + "Kit Passes")
                .lore(lore)
                .amount(1).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
    }
}
