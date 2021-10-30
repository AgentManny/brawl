package rip.thecraft.brawl.duelarena.menu;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DuelMenu extends Menu {
    {
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    private Player target;

    @Override
    public String getTitle(Player player) {
        return "Duel " + target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int x = 1;
        int y = 1;
        for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
            buttons.put(getSlot(x, y), new LoadoutButton(loadout));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private class LoadoutButton extends Button {

        private final MatchLoadout loadout;

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public byte getDamageValue(Player player) {
            return super.getDamageValue(player);
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(ChatColor.DARK_GRAY + "\u00bb" + ChatColor.GREEN + " Click to select " + loadout.getColor() + loadout.getName());
            return new ItemBuilder(loadout.getIcon())
                    .data(loadout.getIconData())
                    .name(loadout.getColor() + CC.BOLD + loadout.getName())
                    .lore(lore)
                    .create();

        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.getPlayerState() != PlayerState.ARENA) {
                player.sendMessage(ChatColor.RED + "You must be in the Duel Arena to send duel requests.");
                return;
            }

            if (target != null || Brawl.getInstance().getPlayerDataHandler().getPlayerData(target).getPlayerState() == PlayerState.ARENA) {
                if (clickType == ClickType.MIDDLE) {
                    player.sendMessage(ChatColor.RED + "Customizable duels have been disabled.");
                } else {
                    Brawl.getInstance().getMatchHandler().sendDuel(player, target, loadout);
                    player.closeInventory();
                }
            } else {
                player.sendMessage((target == null ? "Player" : target.getDisplayName()) + ChatColor.RED + " is not in the duel arena.");
            }
        }

    }
}
