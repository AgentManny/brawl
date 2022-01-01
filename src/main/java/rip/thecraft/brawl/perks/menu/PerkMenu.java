package rip.thecraft.brawl.perks.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.perks.Perk;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.perks.menu.button.PerkInfoButton;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import gg.manny.streamline.util.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Flatfile on 12/21/2021.
 */
public class PerkMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Perk Selector";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        buttons.put(4, getPerkPreview());

        int id = 11;
        int slot = 0;
        for (Perk perk : data.getActivePerks()) {
            buttons.put(++id, new PerkInfoButton(++slot, perk));
        }

        buttons.put(26, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Close";
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 1;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
            }
        });

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    private Button getPerkPreview() {
        String description = "A selection of unique perks that can be stacked upon your selected kit.";

        List<String> lines = ItemBuilder.wrap(description, CC.GRAY, 30, false);
        ItemStack perkInfo = new ItemBuilder(Material.BREWING_STAND_ITEM)
                .name(ChatColor.LIGHT_PURPLE + "Perks")
                .lore(lines)
                .create();

        return Button.fromItem(perkInfo);
    }

}
