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
public class RefillCommand {

    @Command(names = {"refill"})
    public static void execute(Player player){
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        if(data.isDuelArena()){
            player.sendMessage(ChatColor.RED + "You cannot use that here.");
            return;
        }

        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if(game != null){
            if(game.containsPlayer(player)){
                player.sendMessage(ChatColor.RED + "You cannot use this command while in an event.");
                return;
            }
        }

        if(data.getSelectedKit() == null){
            player.sendMessage(ChatColor.RED + "You need to have a kit equipped to use this command.");
            return;
        }

        if(EconUtil.canAfford(data, 200)){
            if(player.getInventory().firstEmpty() == -1){
                player.sendMessage(ChatColor.RED + "Your inventory is full.");
                return;
            }

            Cooldown refill = data.getCooldown("REFILL");
            if (refill != null && !refill.hasExpired()) {
                player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + refill.getTimeLeft() + ChatColor.RED + " before using /refill again.");
                return;
            }
            data.addCooldown("REFILL", TimeUnit.SECONDS.toMillis(60));


            ItemStack item = data.getRefillType().getItem();
            if (item.getType() != Material.AIR) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                }
            }

            player.updateInventory();
            player.sendMessage(ChatColor.YELLOW + "You have purchased a refill for " + ChatColor.LIGHT_PURPLE + "200 credits" + ChatColor.YELLOW + ".");
            EconUtil.withdraw(data, 200);
        }else{
            player.sendMessage(ChatColor.RED + "You don't have enough credits to purchase a refill.");
        }
    }

}
