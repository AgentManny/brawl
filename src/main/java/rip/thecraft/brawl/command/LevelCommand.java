package rip.thecraft.brawl.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.levels.Levels;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;

public class LevelCommand {

    @Command(names = "level")
    public static void execute(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Level level = playerData.getLevel();
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Player Level");
        player.sendMessage(ChatColor.WHITE + "Level: " + ChatColor.LIGHT_PURPLE + Levels.getPrefix(level));
        player.sendMessage(ChatColor.WHITE + "Experience: " + ChatColor.LIGHT_PURPLE + level.getCurrentExp() + "/" + level.getMaxExperience() + ChatColor.GRAY + " (" + level.getPercentageExp() + "%)");
        player.sendMessage(" ");
    }
}
