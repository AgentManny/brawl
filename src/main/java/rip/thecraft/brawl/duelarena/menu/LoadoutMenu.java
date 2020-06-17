package rip.thecraft.brawl.duelarena.menu;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LoadoutMenu extends Menu {
    {
        setAutoUpdate(true);
    }

    private final Brawl plugin;
    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return queueType.getName() + " Queue";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        int i = 0;
        for (MatchLoadout loadout : plugin.getMatchHandler().getLoadouts()) {
            buttonMap.put(i++, new LoadoutButton(loadout, queueType));
        }

        return buttonMap;
    }

    @RequiredArgsConstructor
    private class LoadoutButton extends Button {

        private final MatchLoadout loadout;
        private final QueueType queueType;

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            List<String> lore = new ArrayList<>();
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 25));
            lore.add(CC.DARK_PURPLE + "Queued: " + CC.LIGHT_PURPLE + plugin.getMatchHandler().getQueued(loadout, queueType));
            lore.add(CC.DARK_PURPLE + "Playing: " + CC.LIGHT_PURPLE + plugin.getMatchHandler().getPlaying(loadout));
            if (queueType == QueueType.RANKED) {
                lore.add(" ");
                lore.add(CC.DARK_PURPLE + "Your Elo: " + CC.LIGHT_PURPLE + playerData.getStatistic().get(loadout));
            }
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 25));
            return new ItemBuilder(loadout.getIcon())
                    .data(loadout.getIconData())
                    .name(loadout.getColor() + CC.BOLD + loadout.getName())
                    .lore(lore)
                    .create();

        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (queueType == QueueType.UNRANKED) {
                plugin.getMatchHandler().joinUnrankedQueue(player, loadout);
            } else if (queueType == QueueType.RANKED) {
                plugin.getMatchHandler().joinRankedQueue(player, loadout);
            } else {
                player.sendMessage(ChatColor.RED + "This queue isn't supported.");
            }
            player.closeInventory();
        }

    }
}
