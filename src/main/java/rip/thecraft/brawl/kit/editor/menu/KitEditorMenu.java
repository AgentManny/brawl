package rip.thecraft.brawl.kit.editor.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class KitEditorMenu extends Menu {

    private Kit kit;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        return buttons;
    }

}
