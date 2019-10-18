package gg.manny.brawl.leaderboard.menu;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.leaderboard.menu.button.EloButton;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.Menu;
import org.bukkit.entity.Player;

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
