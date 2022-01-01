package rip.thecraft.brawl.kit.ability.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.AbilityHandler;
import rip.thecraft.brawl.kit.ability.CustomAbility;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.pagination.PaginatedMenu;
import gg.manny.streamline.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbilityMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Abilities";
    }

    public int getMaxItemsPerPage(Player player) {
        return 14;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
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
            lore.add(" ");
            KitHandler kh = Brawl.getInstance().getKitHandler();
            AbilityHandler ah = Brawl.getInstance().getAbilityHandler();
            String kits = kh.getKits().stream()
                    .filter(kit -> kit.getAbilities().contains(ability))
                    .map(Kit::getName)
                    .collect(Collectors.joining(", "));

            String abilities = ah.getCustomAbilities().values().stream()
                    .filter(cability -> cability.getParent().getName().equals(ability.getName()))
                    .map(CustomAbility::getName)
                    .collect(Collectors.joining(", "));

            lore.add(ChatColor.GRAY + "Kits: " + ChatColor.WHITE + kits + (kits.isEmpty() ? ChatColor.RED + "None" : kits));
            lore.add(ChatColor.GRAY + "Variants: " + ChatColor.YELLOW + (abilities.isEmpty() ? ChatColor.RED + "None" : abilities));
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Click to modify");
            return lore;
        }
    }
}
