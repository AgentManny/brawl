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
        setPlaceholder(true);
        setAutoUpdate(true);
    }
    
    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return queueType.getName() + " Queue";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        int x = 1;
        int y = 1;
        for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
            buttonMap.put(getSlot(x, y), new LoadoutButton(loadout, queueType));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }

        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
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
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            List<String> lore = new ArrayList<>();

            lore.add(CC.GRAY + "Queued: " + CC.WHITE + Brawl.getInstance().getMatchHandler().getQueued(loadout, queueType));
            lore.add(CC.GRAY + "Playing: " + CC.WHITE + Brawl.getInstance().getMatchHandler().getPlaying(loadout));
            if (queueType == QueueType.RANKED) {
                lore.add(" ");
                lore.add(CC.GRAY + "Your Elo: " + CC.LIGHT_PURPLE + playerData.getStatistic().get(loadout));
            }
            lore.add(" ");
            lore.add(CC.GRAY + "\u00bb " + ChatColor.GREEN + "Click to join this queue" + CC.GRAY + " \u00ab");

            // Add liners
            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return new ItemBuilder(loadout.getIcon())
                    .data(loadout.getIconData())
                    .name(loadout.getColor() + CC.BOLD + loadout.getName())
                    .lore(lore)
                    .create();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (queueType == QueueType.UNRANKED) {
                Brawl.getInstance().getMatchHandler().joinUnrankedQueue(player, loadout);
            } else if (queueType == QueueType.RANKED) {
                Brawl.getInstance().getMatchHandler().joinRankedQueue(player, loadout);
            } else {
                player.sendMessage(ChatColor.RED + "This queue isn't supported.");
            }
            player.closeInventory();
        }

    }
}
