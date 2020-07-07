package rip.thecraft.brawl.kit.editor.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.buttons.KitAbilityEditButton;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.Map;

@AllArgsConstructor
public class KitAbilityEditMenu extends Menu {

    private Kit kit;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        for (Ability ability : kit.getAbilities()) {
            buttonMap.put(buttonMap.size(), new KitAbilityEditButton(kit, ability));
        }

        return buttonMap;
    }

}
