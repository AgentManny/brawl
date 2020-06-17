package rip.thecraft.brawl.leaderboard.menu;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.leaderboard.menu.button.StatisticButton;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Leaderboards ";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1, y = 0;
        for(StatisticType stat : StatisticType.values()) {
            buttons.put(getSlot(x, y), new StatisticButton(stat));
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }
        return buttons;
    }
}
