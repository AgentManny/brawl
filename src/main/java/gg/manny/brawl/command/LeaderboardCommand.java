package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.leaderboard.menu.LeaderboardMenu;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaderboardCommand {

    @Command(names = { "leaderboards", "lb" })
    public void execute(Player sender) {
        new LeaderboardMenu().openMenu(sender);
    }

    @Command(names = { "leaderboards update", "lb update" }, permission = "op")
    public void update(Player sender) {
        Brawl.getInstance().getLeaderboard().update();
        sender.sendMessage(ChatColor.GREEN + "Manually updated leaderboards");
    }

}
