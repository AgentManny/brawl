package rip.thecraft.brawl.kit.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.menu.KitUnlockMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import gg.manny.streamline.util.ItemBuilder;

import java.util.List;


public class KitUnlockButton extends Button {

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
        List<String> lore = ItemBuilder.wrap("Unlock kits by grinding experience towards an exclusive kit", CC.GRAY, 30, false);
        lore.add("");

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Kit unlockingKit = playerData.getUnlockingKit();

        lore.add(ChatColor.GRAY + "Unlocking Kit: " + (unlockingKit == null ? ChatColor.RED + "None" : ChatColor.YELLOW + unlockingKit.getName()));
        lore.add(" ");

        lore.add(CC.GRAY + "\u00bb " + CC.YELLOW + "Click to unlock a kit");
        return new ItemBuilder(Material.EXP_BOTTLE)
                .name(CC.YELLOW + ChatColor.BOLD + "Unlock Kits")
                .lore(lore)
                .amount(1).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new KitUnlockMenu(player).open(player);
    }
}
