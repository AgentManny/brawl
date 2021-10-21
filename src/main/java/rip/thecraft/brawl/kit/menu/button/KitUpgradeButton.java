package rip.thecraft.brawl.kit.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.unlock.UnlockMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.List;


public class KitUpgradeButton extends Button {

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
        List<String> lore = ItemBuilder.wrap("Upgrade kits to increase the potency of abilities.", CC.GRAY, 30);
        lore.add("");

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Kit unlockingKit = playerData.getUnlockingKit();

        lore.add(ChatColor.GRAY + "Unlocking Kit: " + (unlockingKit == null ? ChatColor.RED + "None" : ChatColor.YELLOW + unlockingKit.getName()));
        lore.add(" ");

        lore.add(CC.GRAY + "\u00bb " + CC.RED + "Still in development");
        return new ItemBuilder(Material.CHEST)
                .name(CC.LIGHT_PURPLE + ChatColor.BOLD + "Kit Upgrades")
                .lore(lore)
                .amount(1).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new UnlockMenu(player).open(player);
    }
}
