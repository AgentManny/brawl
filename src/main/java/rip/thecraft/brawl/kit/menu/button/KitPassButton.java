package rip.thecraft.brawl.kit.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.menu.KitPassMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.ArrayList;
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
        List<String> lore = new ArrayList<>();

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        lore.add(ChatColor.GRAY + "Kit Passes: " + ChatColor.WHITE + playerData.getKitPasses());
        lore.add(" ");

        lore.add(CC.GRAY + "\u00bb " + CC.GREEN + "Click to use a kit pass");
        return new ItemBuilder(Material.PAPER)
                .name(CC.GREEN + ChatColor.BOLD + "Kit Passes")
                .lore(lore)
                .amount(1).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new KitPassMenu(player).open(player);
    }
}
