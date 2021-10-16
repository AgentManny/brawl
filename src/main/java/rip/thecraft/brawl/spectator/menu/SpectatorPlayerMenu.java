package rip.thecraft.brawl.spectator.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.pagination.PaginatedMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectatorPlayerMenu extends PaginatedMenu {
    {
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player && online.canSee(player) && !(online.hasMetadata("hidden") || online.hasMetadata("staffmode"))) {
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(online);
                PlayerState state = playerData.getPlayerState();
                buttons.put(i, new SpectatorPlayerButton(online, state));
                i++;
            }
        }
        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Spectate Player";
    }


    @AllArgsConstructor
    private class SpectatorPlayerButton extends Button {

        private final Player target;
        private final PlayerState state;

        @Override
        public String getName(Player player) {
            return ChatColor.WHITE + target.getDisplayName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lines = new ArrayList<>();
            lines.add(state.getColor() + state.getDisplayName());
            return lines;
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 3;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            SpectatorMode spectatorMode = Brawl.getInstance().getSpectatorManager().getSpectator(player);
            if (spectatorMode == null) {
                player.sendMessage(ChatColor.RED + "You aren't in spectator mode!");
                return;
            }

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Target is no longer online.");
                return;
            }


            spectatorMode.spectate(target);
        }
    }
}
