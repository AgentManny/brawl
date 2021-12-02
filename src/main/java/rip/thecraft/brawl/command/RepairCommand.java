package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.util.Cooldown;

import java.util.concurrent.TimeUnit;

/**
 * Created by Flatfile on 10/21/2021.
 */
public class RepairCommand {

    @Command(names = {"repair"})
    public static void execute(Player player){
        player.sendMessage(ChatColor.RED + "This command is currently disabled.");
    }

}
