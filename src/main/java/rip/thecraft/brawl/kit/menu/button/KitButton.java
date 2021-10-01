package rip.thecraft.brawl.kit.menu.button;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.List;


@RequiredArgsConstructor
public class KitButton extends Button {

    private final Kit kit;

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 30, false);
        lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
//            lore.add(playerData.hasKit(kit) ? CC.GREEN + CC.BOLD + "UNLOCKED" : (kit.getRankType() != RankType.NONE ? CC.GRAY + "Exclusive to " + CC.WHITE + kit.getRankType().getDisplayName() : CC.GRAY + "Price: " + CC.WHITE + kit.getPrice() + " coins"));
        if (!kit.getDescription().isEmpty()) {
            lore.add("");
        }
        KitStatistic statistic = playerData.getStatistic().get(kit);
        lore.add(CC.GRAY + "Kills: " + CC.WHITE + (statistic == null ? 0 : statistic.getKills()));
        lore.add(CC.GRAY + "Deaths: " + CC.WHITE + (statistic == null ? 0 : statistic.getDeaths()));
        lore.add(CC.GRAY + "Uses: " + CC.WHITE + (statistic == null ? 0 : statistic.getUses()));
        if (statistic != null) {
            statistic.getProperties().forEach((name, value) -> lore.add(CC.GRAY + WordUtils.capitalizeFully(name.toLowerCase().replace("_", " ")) + ": " + CC.WHITE + value));
        }
        lore.add("");

        String value;
        boolean buy = false;
        if (playerData.hasKit(kit)) {
            value = CC.GREEN + "Click to use this kit";
        } else if (kit.getRankType() != RankType.NONE) {
            value = CC.RED + "Exclusive to " + kit.getRankType().getDisplayName() + CC.RED + " rank";
            buy = true;
        } else {
            value = CC.RED + "Purchase this kit for " + CC.YELLOW + Math.round(kit.getPrice()) + CC.RED + " credits";
            buy = true;
        }

        if (buy && KitHandler.FREE_KITS) {
            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "FREE KIT" + ChatColor.GRAY + " (Beta access)");
        }
        lore.add(CC.GRAY + "\u00bb " + value + CC.GRAY + " \u00ab");
        lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        return new ItemBuilder(kit.getIcon()).name((playerData.hasKit(kit) ? CC.GREEN : CC.RED) + kit.getName()).amount(1).lore(lore).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        // TODO ADD A FREE KIT IMPLEMENTATION

        if (playerData.hasKit(kit)) {
            kit.apply(player, true, true);
        } else {
            if (KitHandler.FREE_KITS) {
                kit.apply(player, true, true);
                player.sendMessage(CC.GRAY + "* " + ChatColor.WHITE + kit.getName() + ChatColor.GRAY + " is free for a limited time.");
            } else {
                player.sendMessage(CC.RED + "You don't have permission to use this kit.");
            }
        }
    }
}
