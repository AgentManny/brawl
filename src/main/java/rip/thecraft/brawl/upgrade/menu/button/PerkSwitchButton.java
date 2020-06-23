package rip.thecraft.brawl.upgrade.menu.button;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.menus.ConfirmMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class PerkSwitchButton extends Button {

    private PlayerData playerData;

    private Perk perk;
    private int slot;

    private boolean active;

    @Override
    public String getName(Player player) {
        return (active ? ChatColor.GREEN : ChatColor.RED) + perk.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = new ArrayList<>();
        lines.addAll(Arrays.asList(perk.getLore()));
        lines.add(" ");

        String value;
        if (active) {
            value = CC.GREEN + "Perk is already selected";
        } else if (playerData.hasPerk(perk)) {
            value = CC.YELLOW + "Click to use this perk";
        } else {
            value = CC.RED + "Purchase this perk for " + CC.YELLOW + perk.getCredits() + CC.RED + " credits";
        }
        lines.add(CC.GRAY + "\u00bb " + value + CC.GRAY + " \u00ab");

        // Add liners
        lines.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        lines.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        return lines;
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
        if (active) {
            player.sendMessage(ChatColor.RED + "This perk is already active.");
        } else if (playerData.hasPerk(perk)) {
            player.sendMessage(ChatColor.YELLOW + "You have chosen the " + ChatColor.LIGHT_PURPLE + perk.getName() + ChatColor.YELLOW + " perk for " + ChatColor.WHITE + "Slot #" + this.slot + ChatColor.YELLOW + ".");
            playerData.getActivePerks()[this.slot - 1] = perk;
        } else {
            PlayerStatistic statistic = playerData.getStatistic();
            if (playerData.getStatistic().get(StatisticType.CREDITS) < perk.getCredits()) {
                player.sendMessage(ChatColor.RED + "You don't have enough credits.");
                return;
            }

            new ConfirmMenu("Are you sure?", data -> {
                if (data) {
                    statistic.set(StatisticType.CREDITS, statistic.get(StatisticType.CREDITS) - perk.getCredits());
                    playerData.getUnlockedPerks().add(perk);
                    playerData.getActivePerks()[this.slot - 1] = perk;
                    player.sendMessage(ChatColor.YELLOW + "You've purchased " + ChatColor.LIGHT_PURPLE + perk.getName() + ChatColor.YELLOW + " perk for " + perk.getCredits() + " credits.");
                    player.sendMessage(ChatColor.GRAY + "Selected " + ChatColor.WHITE + perk.getName() + ChatColor.GRAY + " perk for " + ChatColor.WHITE + "Slot #" + this.slot + ChatColor.GRAY + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "Confirmation cancelled!");
                }
            }).openMenu(player);
        }
    }
}
