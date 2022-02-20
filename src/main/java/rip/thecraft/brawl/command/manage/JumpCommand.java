package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

public class JumpCommand {

    @Command(names = "jump", permission = "op", hidden = true)
    public static void jump(Player player, double x, double y, double z) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.getSpawnData().throwPlayer(new Location(player.getWorld(), x, y, z));
        player.sendMessage(ChatColor.GREEN + "Launching: " + ChatColor.WHITE + x + ", " + y + ", " + z);
    }

}
