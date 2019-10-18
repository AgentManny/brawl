package gg.manny.brawl.game.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.menu.GameSelectorMenu;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class HostCommand {

    private final Brawl plugin;
    
    @Command(names = { "host", "event", "game" }, description = "Host an event")
    public void execute(Player sender) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isSpawnProtection()) {
            new GameSelectorMenu(plugin).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to host events.");
        }
    }

}