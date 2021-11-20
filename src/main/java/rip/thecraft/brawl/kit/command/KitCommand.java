package rip.thecraft.brawl.kit.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class KitCommand {

    @Command(names = { "kit", "k" })
    public static void apply(Player player, Kit kit) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (!playerData.isSpawnProtection()) {
            player.sendMessage(ChatColor.RED + "You must have spawn protection to select your kit.");
            return;
        }

        if (playerData.hasKit(kit)) {
            if (playerData.isWarp()) {
                player.sendMessage(ChatColor.RED + "You can't change kits when in this area.");
                return;
            }
            kit.apply(player, true, true);
        } else {
            player.sendMessage(CC.RED + "You don't have permission to use this kit.");
        }
    }
}
