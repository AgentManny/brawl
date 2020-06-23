package rip.thecraft.brawl.upgrade.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.upgrade.menu.button.PerkInfoButton;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        buttons.put(11, getPerkPreview());

        int id = 18;
        int slot = 0;
        for (Perk perk : playerData.getActivePerks()) {
            buttons.put(++id, new PerkInfoButton(++slot, perk));
        }

        buttons.put(15, getKillstreakPreview());
        id = 22;
        slot = 0;
        // todo killstreak perk
        buttons.put(++id, new PerkInfoButton(++slot, null));
        buttons.put(++id, new PerkInfoButton(++slot, null));
        buttons.put(++id, new PerkInfoButton(++slot, null));

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    private Button getPerkPreview() {
        String description = "A selection of unique perks that can be stacked upon your selected kit.";

        List<String> lines = ItemBuilder.wrap(description, CC.GRAY, 30);
        ItemStack perkInfo = new ItemBuilder(Material.BREWING_STAND_ITEM)
                .name(ChatColor.LIGHT_PURPLE + "Perks")
                .lore(lines)
                .create();

        return Button.fromItem(perkInfo);
    }

    private Button getKillstreakPreview() {
        String description = "Killstreaks are specific rewards that are triggered every time you get X amount of kills.";

        List<String> lines = ItemBuilder.wrap(description, CC.GRAY, 30);
        ItemStack perkInfo = new ItemBuilder(Material.BLAZE_POWDER)
                .name(ChatColor.RED + "Killstreaks")
                .lore(lines)
                .create();

        return Button.fromItem(perkInfo);
    }
}
