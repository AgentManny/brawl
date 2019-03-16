package gg.manny.brawl.game.menu;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.GameType;
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
public class GameSelectorMenu extends Menu {
    {
        this.setPlaceholder(true);
        setAutoUpdate(true);
    }

    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        return "Game Selector";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        int x = 1;
        int y = 1;

        for (GameType type : GameType.values()) {
            buttonMap.put(getSlot(x, y), new GameButton(plugin, type));
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
    private class GameButton extends Button {

        private final Brawl plugin;
        private final GameType gameType;

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            List<String> lore = ItemBuilder.wrap(gameType.getDescription(), CC.GRAY, 30);
            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            lore.add("");
            lore.add(CC.GRAY + "\u00bb " + CC.GREEN + (playerData.hasGame(gameType) ? "Click to play this game" : CC.RED + "Exclusive to " + gameType.getRankType().getDisplayName() + CC.RED + " rank.") + CC.GRAY + " \u00ab");
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return new ItemBuilder(gameType.getIcon())
                    .name((playerData.hasGame(gameType) ? CC.GREEN : CC.RED) + gameType.getName())
                    .lore(lore)
                    .create();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if(playerData.hasGame(gameType)) {

                //TODO CREATE GAME kit.apply(player, true, true);
            } else {
                player.sendMessage(CC.RED  + "You don't have permission to use this game.");
            }
        }
    }

}
