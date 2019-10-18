package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
