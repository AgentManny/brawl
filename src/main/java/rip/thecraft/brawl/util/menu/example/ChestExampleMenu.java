package rip.thecraft.brawl.util.menu.example;

import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.brawl.util.menu.MenuOption;
import rip.thecraft.brawl.util.menu.MenuRows;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ChestExampleMenu extends Menu {

    public ChestExampleMenu() {
        super("Private Chest", MenuRows.THREE, MenuOption.ALLOW_INTERACT);
        MenuButton button = this.addButton(0, 0, new MenuButton(Material.REDSTONE, ChatColor.RED + "Go back", ChatColor.GRAY + "Go somewhere else lol"));
        button.setClick((p, data) -> {
            button.createError(data, "Nice try fool.", "There isn't a previous page", 20L);
            p.sendMessage(ChatColor.RED + "You can't go back fool.");
        });
        button.setUpdate(item -> new ItemStack(Material.REDSTONE, new Random().nextInt(10)));
        setUpdateTicks(1L);
    }
}