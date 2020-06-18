package rip.thecraft.brawl.upgrade.menu.button;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.List;

@AllArgsConstructor
public class PerkInfoButton extends Button {

    private int slot;
    private Perk perk;

    @Override
    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + "Perk Slot #" + slot;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = ItemBuilder.wrap(perk == null ? "You don't have a perk selected for this slot." : perk.getDescription(), CC.GRAY, 30);

        lines.add(" ");
        lines.add(ChatColor.GRAY + "Selected: " + ChatColor.LIGHT_PURPLE + perk.getName());
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
        return (byte) (perk == null ? 8  : 0);
    }
}
