package gg.manny.brawl.market;

import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.pivot.util.menu.Button;
import gg.manny.pivot.util.menu.Menu;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MarketMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        return buttons;
    }

    private class InventoryFillButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MUSHROOM_SOUP)
                    .name(CC.GREEN + "Fill Inventory")
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hotbar) {
            while (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
            }
        }

    }
}
