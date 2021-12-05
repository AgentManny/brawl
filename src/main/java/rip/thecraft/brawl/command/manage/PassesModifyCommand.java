package rip.thecraft.brawl.command.manage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

import java.util.Map;

public class PassesModifyCommand {

    @Command(names = "pass add", permission = "op")
    public static void addPass(CommandSender sender, @Param(defaultValue = "self") PlayerData playerData, @Param(name = "passes") int passes) {
        playerData.setKitPasses(playerData.getKitPasses() + passes);
        sender.sendMessage(ChatColor.GREEN + "Added " + passes + " global kit pass to " + sender.getName() + ".");
    }

    @Command(names = "pass set", permission = "op")
    public static void setPass(CommandSender sender, @Param(defaultValue = "self") PlayerData playerData, @Param(name = "passes") int passes) {
        playerData.setKitPasses(passes);
        sender.sendMessage(ChatColor.GREEN + "Set " + playerData.getName() + " global kit pass to " + passes + ".");
    }

    @Command(names = "pass giveall", permission = "op")
    public static void giveAllPass(CommandSender sender) {
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(onlinePlayer);
            playerData.setKitPasses(playerData.getKitPasses() + 1);
            onlinePlayer.sendMessage(ChatColor.GREEN + "You were given a " + ChatColor.BOLD + "Kit Pass" + ChatColor.GREEN + " from " + sender.getName() + ChatColor.GREEN + ".");
        }
    }


    @Command(names = "pass info", permission = "op")
    public static void getPasses(CommandSender sender, @Param(defaultValue = "self") PlayerData playerData) {
        sender.sendMessage(ChatColor.GREEN + "Pass info for: " + playerData.getName());
        sender.sendMessage(ChatColor.GREEN + "Kit Passes: " + playerData.getKitPasses());
        sender.sendMessage(ChatColor.GREEN + "Global Kit Passes: " + playerData.getGlobalKitPass());
        sender.sendMessage(ChatColor.GREEN + "Kits:");
        for (Map.Entry<String, KitStatistic> entry : playerData.getStatistic().getKitStatistics().entrySet()) {
            String key = entry.getKey();
            KitStatistic value = entry.getValue();
            if (value.getTrialPass() <= 1) {
                sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + key + ChatColor.GRAY + ": " + ChatColor.YELLOW + value.getTrialPass());
            }
        }
    }
}
