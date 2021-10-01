package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class ExpModifyCommand {

    @Command(names = "exp add", permission = "op")
    public static void addExp(CommandSender sender, @Param(defaultValue = "self") PlayerData playerData, @Param(name = "experience") int exp) {
        Level level = playerData.getLevel();
        level.addExp(playerData.getPlayer(), exp, "Added by " + sender.getName());
        sender.sendMessage(ChatColor.GREEN + "Added " + exp + " experience to " + sender.getName() + ".");
        sender.sendMessage(ChatColor.WHITE + playerData.getName() + ChatColor.GREEN + " experience is now " + level.getCurrentExp() + "/" + level.getMaxExperience() + " (" + level.getCurrentLevel() + "L)");
    }

    @Command(names = "exp set", permission = "op")
    public static void setExp(CommandSender sender, @Param(defaultValue = "self") PlayerData playerData, @Param(name = "experience") int exp) {
        Level level = playerData.getLevel();
        level.setCurrentExp(exp);
        level.addExp(playerData.getPlayer(), 0, "Debug");

        sender.sendMessage(ChatColor.GREEN + "Set " + exp + " experience to " + sender.getName() + ".");
        sender.sendMessage(ChatColor.WHITE + playerData.getName() + ChatColor.GREEN + " experience is now " + level.getCurrentExp() + "/" + level.getMaxExperience() + " (" + level.getCurrentLevel() + "L)");
    }

}
