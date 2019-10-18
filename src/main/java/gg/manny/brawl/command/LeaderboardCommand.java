package gg.manny.brawl.command;

import gg.manny.brawl.leaderboard.menu.LeaderboardMenu;
import gg.manny.quantum.command.Command;
import org.bukkit.entity.Player;

public class LeaderboardCommand {

    @Command(names = { "leaderboards", "lb" })
    public void execute(Player sender) {
        new LeaderboardMenu().openMenu(sender);
    }

}
