package rip.thecraft.brawl.kit.editor.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.action.KitEditAction;
import rip.thecraft.brawl.kit.editor.buttons.KitEditButton;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.Map;

@AllArgsConstructor
public class KitEditMenu extends Menu {

    private Kit kit;

    @Override
    public String getTitle(Player player) {
        return CC.GOLD + "Editting " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        for (KitEditAction action : KitEditAction.values()) {
            buttonMap.put(buttonMap.size(), new KitEditButton(kit, action));
        }

        return buttonMap;
    }


}
