package rip.thecraft.brawl.game.menu;

import gg.manny.hologram.HologramPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import gg.manny.streamline.util.ItemBuilder;
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
            if(type.isHidden()) continue;
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
            boolean disabled = gameType.isDisabled();
            boolean noMaps = Brawl.getInstance().getGameHandler().getMapHandler().getMaps(gameType).isEmpty();
            boolean access = playerData.hasGame(gameType);

            List<String> lore = ItemBuilder.wrap(gameType.getDescription(), CC.GRAY, 32, false);
            lore.add(0, " ");
            lore.add(0, ChatColor.DARK_GRAY + (access ? "Unlocked" : "Locked"));
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Credits: " + ChatColor.GOLD + Game.HOST_CREDITS + " credits");
            long cooldown = Brawl.getInstance().getGameHandler().getCooldown().getOrDefault(gameType, 0L);
            if (System.currentTimeMillis() < cooldown) {
                lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(cooldown - System.currentTimeMillis())));
            }
            lore.add(" ");
            lore.add(CC.GRAY + "\u00bb " + (disabled ? ChatColor.RED + "Game is currently disabled :("  : noMaps ? ChatColor.RED + "No maps available" : (playerData.hasGame(gameType) ? ChatColor.GREEN + "Click to play this game" : CC.RED + "Exclusive to " + gameType.getRankType().getDisplayName() + CC.RED + " rank")));
            ItemBuilder item = new ItemBuilder(gameType.getIcon())
                    .name((playerData.hasGame(gameType) && !noMaps ? CC.GREEN : CC.RED) + CC.BOLD + gameType.getName())
                    .lore(lore);
            if (disabled) {
                boolean legacyVersion = HologramPlugin.getInstance().onLegacyVersion(player);
                item.material(legacyVersion ? Material.STAINED_GLASS_PANE : Material.BARRIER);
                item.data(legacyVersion ? (byte) 14 : 0);
            }
            return item.create();
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
            player.performCommand("game host " + gameType.name().toLowerCase());
        }
    }

}
