package rip.thecraft.brawl.upgrade.menu;

import org.bukkit.entity.Player;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class UpgradeMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
