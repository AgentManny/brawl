package gg.manny.brawl.duelarena.menu;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerState;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.Menu;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.server.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DuelMenu extends Menu {

    private Brawl plugin;
    private Player target;

    @Override
    public String getTitle(Player player) {
        return "Duel " + target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for (MatchLoadout loadout : plugin.getMatchHandler().getLoadouts()) {
            buttons.put(i++, new LoadoutButton(loadout));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private class LoadoutButton extends Button {

        private final MatchLoadout loadout;

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
//            List<String> lore = ItemBuilder.wrap(CC.AQUA + "Middle Click" + CC.YELLOW + " to customize match values.", CC.YELLOW, 25);
//            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 25));
//            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 25));

            return new ItemBuilder(loadout.getIcon())
                    .data(loadout.getIconData())
                    .name(loadout.getColor() + CC.BOLD + loadout.getName())
           //         .lore(lore)
                    .create();

        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData.getPlayerState() != PlayerState.ARENA) {
                player.sendMessage(ChatColor.RED + "You must be in the Duel Arena to send duel requests.");
                return;
            }

            if (target != null || plugin.getPlayerDataHandler().getPlayerData(target).getPlayerState() == PlayerState.ARENA) {
                if (clickType == ClickType.MIDDLE) {
                    player.sendMessage(ChatColor.RED + "Customizable duels have been disabled.");
                } else {
                    plugin.getMatchHandler().sendDuel(player, target, loadout);
                    player.closeInventory();
                }
            } else {
                player.sendMessage((target == null ? "Player" : target.getDisplayName()) + ChatColor.RED + " is not in the duel arena.");
            }
        }

    }
}
