package gg.manny.brawl.market.items;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.menus.ConfirmMenu;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.pivot.util.callback.Callback;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
public abstract class MarketItem extends Button {

    private final String name;
    private final Material type;

    protected final int credits;

    @Setter private boolean confirm = false;

    public abstract int getWeight();

    public abstract String getDescription();

    public abstract void purchase(Player player, PlayerData playerData);

    @Override
    public ItemStack getButtonItem(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> lore = ItemBuilder.wrap(getDescription(), CC.GRAY, 30);
        lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        lore.add(" ");
        lore.add(CC.GRAY + "\u00bb " + CC.RED + "Purchase for " + CC.YELLOW + Math.round(this.credits) + " credits" + CC.GRAY + " \u00ab");
        lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        return new ItemBuilder(type)
                .name((playerData.getStatistic().get(StatisticType.CREDITS) > credits ? CC.GREEN : CC.RED) + name)
                .lore(lore)
                .create();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        PlayerStatistic statistic = playerData.getStatistic();
        if (playerData.getStatistic().get(StatisticType.CREDITS) < credits) {
            player.sendMessage(ChatColor.RED + "You don't have enough credits.");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full.");
            return;
        }

        if (confirm) {
            new ConfirmMenu("Are you sure?", (Callback<Boolean>) data -> {
                if (data.booleanValue()) {
                    statistic.set(StatisticType.CREDITS, statistic.get(StatisticType.CREDITS) - credits);
                    player.sendMessage(ChatColor.YELLOW + "You've purchased " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " for " + credits + " credits.");
                } else {
                    player.sendMessage(ChatColor.RED + "Confirmation cancelled.");
                }
            }, true, null, this).openMenu(player);
        } else {
            purchase(player, playerData);
            statistic.set(StatisticType.CREDITS, statistic.get(StatisticType.CREDITS) - credits);
            player.sendMessage(ChatColor.YELLOW + "You've purchased " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " for " + credits + " credits.");
            player.closeInventory();
        }
    }

}
