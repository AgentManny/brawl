package gg.manny.brawl.kit.menu;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.type.RankType;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.pivot.util.menu.Button;
import gg.manny.pivot.util.menu.Menu;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class KitSelectorMenu extends Menu {
    {
        this.setPlaceholder(true);
        setAutoUpdate(true);
    }
    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        return "Kit Selector";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        int x = 1;
        int y = 1;

        for (Kit kit : plugin.getKitHandler().getKits()) {
            buttonMap.put(getSlot(x, y), new KitButton(plugin, kit));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }
        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return super.size(buttons) + 9;
    }

    @RequiredArgsConstructor
    public class KitButton extends Button {

        private final Brawl plugin;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 30);
            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));

            lore.add(CC.GRAY + "Rarity: " + CC.WHITE + kit.getRarityType().getDisplayName());
            lore.add(playerData.hasKit(kit) ? CC.GREEN + CC.BOLD + "UNLOCKED" : (kit.getRankType() != RankType.NONE ? CC.GRAY + "Exclusive to " + CC.WHITE + kit.getRankType().getDisplayName() : CC.GRAY + "Price: " + CC.WHITE + kit.getPrice() + " coins"));
            lore.add("");
            lore.add(CC.GRAY + "\u00bb " + CC.WHITE + (playerData.hasKit(kit) ? "Click to use this kit" : CC.RED + "Click to purchase this kit") + CC.GRAY + " \u00ab");
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return new ItemBuilder(kit.getIcon())
                    .name((playerData.hasKit(kit) ? CC.GREEN : CC.RED) + kit.getName())
                    .lore(lore)
                    .create();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if(playerData.hasKit(kit)) {
                kit.apply(player);
            } else {
                player.sendMessage(CC.RED  + "You don't have permission to use this kit.");
            }
        }
    }

}
