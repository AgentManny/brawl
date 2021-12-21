package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.perks.menu.PerkMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

/**
 * Created by Flatfile on 12/21/2021.
 */
public class PerkCommand {

    @Command(names = { "perk", "perks" })
    public static void execute(Player sender) {
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);

        if(!data.isSpawnProtection()){
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to use this command.");
            return;
        }

        new PerkMenu().openMenu(sender);
    }

}
