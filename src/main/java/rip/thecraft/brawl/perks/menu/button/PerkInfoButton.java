package rip.thecraft.brawl.perks.menu.button;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.perks.menu.ChoosePerkMenu;
import rip.thecraft.brawl.perks.Perk;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class PerkInfoButton extends Button {

    private int slot;
    private Perk perk;

    @Override
    public String getName(Player player) {
        return (perk == null ? ChatColor.RED : ChatColor.LIGHT_PURPLE) + "Perk Slot #" + slot;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = new ArrayList<>();
        if (perk == null) {
            lines.add(ChatColor.GRAY + "You don't have a perk selected for");
            lines.add(ChatColor.GRAY + "this slot.");
        } else {
            lines.addAll(Arrays.asList(perk.getLore()));
            lines.add(" ");
            lines.add(ChatColor.GRAY + "Selected: " + ChatColor.WHITE + perk.getName());
        }

        lines.add(" ");
        lines.add(CC.GRAY + "\u00bb " + CC.YELLOW + "Click to choose a perk" + CC.GRAY + " \u00ab");

        // Add liners
        lines.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        lines.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        return lines;
    }

    @Override
    public int getAmount(Player player) {
        return slot;
    }

    @Override
    public Material getMaterial(Player player) {
        return perk == null ? Material.INK_SACK : perk.getIcon();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) (perk == null ? 8  : perk.getIconData());
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new ChoosePerkMenu(this.slot).openMenu(player);
    }
}
