package rip.thecraft.brawl.kit.ability.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.AbilityHandler;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import gg.manny.streamline.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityVariantsMenu extends Menu {

    private final Ability ability;
    public AbilityVariantsMenu(Ability ability) {
        this.ability = ability;
    }

    @Override
    public String getTitle(Player player) {
        return ability.getName() + " - Variants";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        AbilityHandler abilityHandler = Brawl.getInstance().getAbilityHandler();

        HashMap<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for (Ability ability : abilityHandler.getAbilities().values()) {
            buttons.put(i++, new AbilityInfoButton(ability));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private class AbilityInfoButton extends Button {

        private final Ability ability;

        @Override
        public String getName(Player player) {
            return ability.getColor() + ability.getName();
        }

        @Override
        public Material getMaterial(Player player) {
            return ability.getIcon() == null ? Material.EMPTY_MAP : ability.getIcon();
        }

        @Override
        public byte getDamageValue(Player player) {
            return ability.getData();
        }

        @Override
        public List<String> getDescription(Player player) {
            String description = ability.getDescription();
            List<String> lore = description == null || description.isEmpty() ? new ArrayList<>() : ItemBuilder.wrap(description, CC.GRAY, 30, false);

            return lore;
        }
    }
}
