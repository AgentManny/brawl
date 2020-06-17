package rip.thecraft.brawl.leaderboard.menu;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.leaderboard.menu.button.EloButton;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardEloMenu extends Menu {
    {
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Leaderboards - Elo";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1, y = 0;
        for (MatchLoadout loadout : Brawl.getInstance().getMatchHandler().getLoadouts()) {
            buttons.put(getSlot(x, y), new EloButton(loadout));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }

        return buttons;
    }
}
