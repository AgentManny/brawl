package rip.thecraft.brawl.kit.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.menu.button.KitButton;
import rip.thecraft.brawl.kit.menu.button.KitRandomButton;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitSelectorMenu extends Menu {
    {
        this.setPlaceholder(true);
        setAutoUpdate(true);
    }

    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        return "Kit Selector";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        int x = 1;
        int y = 1;
        for (Kit kit : plugin.getKitHandler().getKits()) {
            buttonMap.put(getSlot(x, y), new KitButton(plugin, kit));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }

        int size = size(buttonMap) - 5;
        buttonMap.put(size + 9, new KitRandomButton(plugin, plugin.getKitHandler().getKits().get(Brawl.RANDOM.nextInt(plugin.getKitHandler().getKits().size()))));
        return buttonMap;
    }

}
