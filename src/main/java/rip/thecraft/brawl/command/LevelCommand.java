package rip.thecraft.brawl.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.levels.Level;
import rip.thecraft.brawl.spawn.levels.Levels;
import rip.thecraft.brawl.spawn.levels.task.LevelFlashTask;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.util.BukkitUtil;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.menu.menus.ConfirmMenu;

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

    @Command(names = "prestige")
    public static void prestige(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Level levelData = playerData.getLevel();
        if (!levelData.canPrestige()) {
            player.sendMessage(ChatColor.RED + "You can only prestige once you have reached " + ChatColor.BOLD + "Level " + Level.MAX_LEVEL + ChatColor.RED + ".");
            int currentExp = levelData.getCurrentExp();
            int maxExp = levelData.getMaxExperience();
            if (levelData.getCurrentLevel() == Level.MAX_LEVEL) {
                if (currentExp < maxExp) {
                    player.sendMessage(ChatColor.GRAY + "You require " + ChatColor.WHITE + (maxExp - currentExp) + ChatColor.GRAY + " more experience to prestige.");
                }
            }
            return;
        }

        player.sendMessage(" ");
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Are you sure you want you want to Prestige?");
        player.sendMessage(ChatColor.GRAY + "You will lose access to Unlocked Kits, Perks and progress for challenges.");
        player.sendMessage(" ");
        player.sendMessage(ChatColor.YELLOW + "> Type " + ChatColor.LIGHT_PURPLE + "/prestige confirm" + ChatColor.YELLOW + " to proceed with prestiging.");
    }

    @Command(names = "prestige confirm")
    public static void prestigeConfirm(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Level levelData = playerData.getLevel();
        if (!levelData.canPrestige()) {
            prestige(player);
            return;
        }

        new ConfirmMenu("Prestige Confirmation", (confirm) -> {
            if (confirm) {
                if (levelData.canPrestige()) {
                    PlayerStatistic statistic = playerData.getStatistic();
                    statistic.set(StatisticType.LEVEL, StatisticType.LEVEL.getDefaultValue());
                    statistic.add(StatisticType.PRESTIGE);
                    levelData.setCurrentExp(0);
                    Bukkit.broadcastMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.GREEN + " has reached " + ChatColor.BOLD + "Level " + Level.MAX_LEVEL + ChatColor.GREEN + " and has prestiged to " + BukkitUtil.romanNumerals(levelData.getPrestige()) + ChatColor.GREEN + ".");
                    player.closeInventory();
                    new LevelFlashTask(player, levelData).runTaskTimer(Brawl.getInstance(), 0, 7L);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You have cancelled your prestige.");
            }
        }).openMenu(player);
    }

}
