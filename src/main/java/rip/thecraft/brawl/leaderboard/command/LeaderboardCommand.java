package rip.thecraft.brawl.leaderboard.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.leaderboard.menu.LeaderboardMenu;
import rip.thecraft.spartan.command.Command;

public class LeaderboardCommand {

    @Command(names = { "leaderboards", "lb" })
    public static void execute(Player sender) {
        new LeaderboardMenu().openMenu(sender);
    }

    @Command(names = { "leaderboards update", "lb update" }, permission = "op")
    public static void update(Player sender) {
        Brawl.getInstance().getLeaderboard().update();
        sender.sendMessage(ChatColor.GREEN + "Manually updated leaderboards");
    }

}
