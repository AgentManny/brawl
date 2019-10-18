package gg.manny.brawl.duelarena.duel;

import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DuelBuilderMenu extends Menu {

    private final DuelBuilder builder;

    @Override
    public String getTitle(Player player) {
        return "Duel " + builder.getTarget().getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        return buttons;
    }



}
