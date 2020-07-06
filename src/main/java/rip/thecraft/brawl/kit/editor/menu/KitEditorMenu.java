package rip.thecraft.brawl.kit.editor.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.buttons.KitSelectButton;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class KitEditorMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.GOLD + "Select a kit to edit.";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            buttons.put(buttons.size(), new KitSelectButton(kit));
        }

        return buttons;
    }

}
