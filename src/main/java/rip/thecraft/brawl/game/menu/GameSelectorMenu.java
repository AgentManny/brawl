package rip.thecraft.brawl.game.menu;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class GameSelectorMenu extends Menu {
    {
        this.setPlaceholder(true);
        setAutoUpdate(true);
    }

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
            buttonMap.put(getSlot(x, y), new GameButton(type));
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

        private final GameType gameType;

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            boolean noMaps = Brawl.getInstance().getGameHandler().getMapHandler().getMaps(gameType).isEmpty();
            List<String> lore = ItemBuilder.wrap(gameType.getDescription(), CC.GRAY, 30);
            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            lore.add("");
            long cooldown = Brawl.getInstance().getGameHandler().getCooldown().getOrDefault(gameType, 0L);
            if (System.currentTimeMillis() < cooldown) {
                lore.add(ChatColor.RED + "Cooldown: " + ChatColor.YELLOW + TimeUtils.formatIntoMMSS((int) TimeUnit.MILLISECONDS.toSeconds(cooldown - System.currentTimeMillis())));
            }
            lore.add(CC.GRAY + "\u00bb " + CC.GREEN + (playerData.hasGame(gameType) ? "Click to play this game" : CC.RED + (noMaps ? "No maps available" : "Exclusive to " + gameType.getRankType().getDisplayName() + CC.RED + " rank.")) + CC.GRAY + " \u00ab");
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return new ItemBuilder(gameType.getIcon())
                    .name((playerData.hasGame(gameType) && !noMaps ? CC.GREEN : CC.RED) + gameType.getName())
                    .lore(lore)
                    .create();
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (Brawl.getInstance().getGameHandler().getMapHandler().getMaps(gameType).isEmpty()) {
                player.sendMessage(ChatColor.RED + "There aren't any maps available for this game.");
                return;
            }

            long cooldown = Brawl.getInstance().getGameHandler().getCooldown().getOrDefault(gameType, 0L);
            if (!player.hasPermission("brawl.game.bypass") && System.currentTimeMillis() < cooldown) {
                player.sendMessage(ChatColor.RED + "This game is under cooldown for another " + TimeUtils.formatIntoDetailedString((int) TimeUnit.MILLISECONDS.toSeconds(cooldown - System.currentTimeMillis())) + ".");
                return;
            }

            if(playerData.hasGame(gameType)) {

                //TODO CREATE GAME kit.apply(player, true, true);
                Brawl.getInstance().getGameHandler().start(player, gameType);
            } else {
                player.sendMessage(CC.RED  + "You don't have permission to use this game.");
            }
        }
    }

}
