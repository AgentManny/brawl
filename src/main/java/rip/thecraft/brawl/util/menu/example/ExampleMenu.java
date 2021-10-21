
package rip.thecraft.brawl.util.menu.example;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.brawl.util.menu.MenuRows;

public class ExampleMenu extends Menu {

    public static ChestExampleMenu GLOBAL_MENU = new ChestExampleMenu();

    public ExampleMenu() {
        super("Example Menu", MenuRows.FOUR);
        MenuButton button = this.addButton(1, 1, new MenuButton(Material.EMERALD, ChatColor.GREEN + "Server", ChatColor.GRAY + "Lore.", ChatColor.YELLOW + "New lore."));
        button.setClick((player, data) -> player.sendMessage(ChatColor.GREEN + "You clicked a server."));
        button = this.addButton(1, 2, new MenuButton(Material.CHEST, "Private Chest", "Your private chest."));
        button.setClick((player, data) -> ExampleMenu.GLOBAL_MENU.open(player));
    }

    static {
        ExampleMenu.GLOBAL_MENU = new ChestExampleMenu();
    }
}