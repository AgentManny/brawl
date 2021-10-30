package rip.thecraft.brawl.command;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

public class ClearkitCommand {

    @Command(names = { "clearkit", "resetkit", "ck", "rk" })
    public static void execute(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.clearKit(true);
    }
}
