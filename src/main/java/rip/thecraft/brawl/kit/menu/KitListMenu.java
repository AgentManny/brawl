package rip.thecraft.brawl.kit.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.pagination.PaginatedMenu;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KitListMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.GOLD + "Kits";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();
        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            buttonMap.put(buttonMap.size(), new KitListButton(kit));
        }
        return buttonMap;
    }

    @AllArgsConstructor
    private class KitListButton extends Button {

        private Kit kit;

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + kit.getName();
        }

        @Override
        public Material getMaterial(Player player) {
            return kit.getIcon().getType();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    CC.GRAY + "Price: " + CC.GREEN + (kit.getPrice() <= 0 ? "Free" : "$" + new DecimalFormat("#,##0.##").format(kit.getPrice())),
                    CC.GRAY + "Weight: " + CC.WHITE + kit.getWeight(),
                    CC.GRAY + "Icon: " + CC.WHITE + WordUtils.capitalizeFully(kit.getIcon().getType().name().toLowerCase().replace("_", " ")),
                    CC.GRAY + "Description: " + CC.WHITE + kit.getDescription(),
                    CC.GRAY + "Armor",
                    CC.GRAY + "  Helmet: " + CC.WHITE + (kit.getArmor().getHelmet() == null ? "None" : WordUtils.capitalizeFully(kit.getArmor().getHelmet().getType().name().toLowerCase().replace("_", " "))),
                    CC.GRAY + "  Chestplate: " + CC.WHITE + (kit.getArmor().getChestplate() == null ? "None" : WordUtils.capitalizeFully(kit.getArmor().getChestplate().getType().name().toLowerCase().replace("_", " "))),
                    CC.GRAY + "  Leggings: " + CC.WHITE + (kit.getArmor().getLeggings() == null ? "None" : WordUtils.capitalizeFully(kit.getArmor().getLeggings().getType().name().toLowerCase().replace("_", " "))),
                    CC.GRAY + "  Boots: " + CC.WHITE + (kit.getArmor().getBoots() == null ? "None" : WordUtils.capitalizeFully(kit.getArmor().getBoots().getType().name().toLowerCase().replace("_", " "))),
                    " ",
                    CC.GRAY + "Click here to edit kit " + CC.WHITE + kit.getName() + CC.GRAY + "."
            );
        }
    }
}
