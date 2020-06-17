package rip.thecraft.brawl.game.command;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.menu.GameSelectorMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
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