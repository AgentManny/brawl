package rip.thecraft.brawl.kit.editor.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.kit.Kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitUpdateMenu {

    private final Player player;
    private final Kit kit;

    private Inventory inventory;

    public KitUpdateMenu(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;

        this.inventory = Bukkit.createInventory(player, 54, "Editing kit: " + kit.getName());
        
        player.openInventory(inventory);
        player.sendMessage(ChatColor.YELLOW + "You are now editing kit: " + ChatColor.GREEN + kit.getName() + ChatColor.YELLOW + ".");
    }

    public void init() {
        int x = 0;
        int y = 0;

        List<ItemStack> targetInv = new ArrayList<>(Arrays.asList(kit.getItems().getItems()));

        // we want the hotbar (the first 9 items) to be at the bottom (end),
        // not the top (start) of the list, so we rotate them.
        for (int i = 0; i < 9; i++) {
            targetInv.add(targetInv.remove(0));
        }

        for (ItemStack inventoryItem : targetInv) {
            inventory.setItem(getSlot(x, y), inventoryItem);

            if (x++ > 7) {
                x = 0;
                y++;
            }
        }

        x = 0; // start armor backwards, helm first
        inventory.setItem(getSlot(++x, y), kit.getArmor().getHelmet());
        inventory.setItem(getSlot(++x, y), kit.getArmor().getChestplate());
        inventory.setItem(getSlot(++x, y), kit.getArmor().getLeggings());
        inventory.setItem(getSlot(++x, y), kit.getArmor().getBoots());
    }

    private int getSlot(int x, int y) {
        return 9 * y + x;
    }
}
