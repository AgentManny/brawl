package rip.thecraft.brawl.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class ClearkitCommand {

    private final Brawl plugin;

    @Command(names = { "clearkit", "resetkit", "ck", "rk" })
    public void execute(Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        Kit kit = playerData.getSelectedKit();
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "You don't have a kit equipped.");
            return;
        }

        if (!playerData.isSpawnProtection()) {
            player.sendMessage(ChatColor.RED + "You must have spawn protection to clear your kit.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Your kit has been cleared.");
        playerData.setPreviousKit(playerData.getSelectedKit());
        playerData.setSelectedKit(null);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        playerData.setSpawnProtection(true);
        plugin.getItemHandler().apply(player, InventoryType.SPAWN);

    }

}
