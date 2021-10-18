package rip.thecraft.brawl.scoreboard;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;

import java.util.List;

public interface ScoreboardProvider {

    Brawl plugin = Brawl.getInstance();

    List<String> getLines(Player player, PlayerData playerData, List<String> lines);

}
