package rip.thecraft.brawl.leaderboard.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.leaderboard.menu.LeaderboardMenu;
import rip.thecraft.brawl.visual.VisualManager;
import rip.thecraft.spartan.command.Command;

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

    @Command(names = { "leaderboards holoset", "lb holoset" }, permission = "op")
    public void holoSet(Player sender) {
        Brawl.getInstance().setLocationByName(VisualManager.PLAYER_HOLO_STATS, sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Set leaderboard holograms");
    }

}
