package rip.thecraft.brawl.spawn.perks.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spawn.perks.Perk;
import rip.thecraft.spartan.menu.Button;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PerkRemoveButton extends Button {

    private PlayerData playerData;

    private int slot;

    @Override
    public String getName(Player player) {
        return ChatColor.RED + "Remove Perk";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.GRAY + "Are you hardcore enough that you");
        lines.add(ChatColor.GRAY + "don't need any perk for this slot?");
        lines.add(" ");
        lines.add(ChatColor.RED + "Remove your perk.");
        return lines;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 1;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Perk selectedPerk = playerData.getActivePerks()[this.slot - 1];
        if (selectedPerk != null) {
            player.sendMessage(ChatColor.YELLOW + "You have removed " + ChatColor.LIGHT_PURPLE + selectedPerk.getName() + ChatColor.YELLOW + " from your Slot #" + this.slot + ".");
            playerData.getActivePerks()[this.slot - 1] = null;
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "You don't have a perk selected for this slot.");
        }
    }
}
