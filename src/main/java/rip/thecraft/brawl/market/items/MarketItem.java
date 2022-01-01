package rip.thecraft.brawl.market.items;

import gg.manny.streamline.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.menus.ConfirmMenu;
import rip.thecraft.spartan.util.Cooldown;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class MarketItem extends Button {

    private final String name;
    private final Material type;

    protected final int credits;

    @Setter private boolean confirm = false;

    public abstract int getWeight();

    public abstract String getDescription();

    @Override
    public String getName(Player player) {
        return name;
    }

    @Override
    public Material getMaterial(Player player) {
        return type;
    }

    public abstract void purchase(Player player, PlayerData playerData);

    public String getCooldown() {
        return null;
    }

    public long getCooldownTime() {
        return -1;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> lore = ItemBuilder.wrap(getDescription(), CC.GRAY, 30, false);
        if (!getDescription().isEmpty()) {
            lore.add(" ");
        }
        if (getCooldown() != null && getCooldownTime() > 0) {
            Cooldown cooldown = playerData.getCooldown(getCooldown());
            if (cooldown != null && !cooldown.hasExpired()) {
                lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(cooldown.getExpire() - System.currentTimeMillis())));
                lore.add(" ");
            }
        }
        lore.add(CC.GRAY + "\u00bb " + CC.RED + "Purchase for " + CC.YELLOW + Math.round(this.credits) + " credits");
        return new ItemBuilder(type)
                .name((playerData.getStatistic().get(StatisticType.CREDITS) > credits ? CC.GREEN : CC.RED) + name)
                .lore(lore)
                .create();
    }

    public boolean getRequiresSpawn() {
        return true;
    }

    public boolean requiresInventorySpace(){
        return false;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (getRequiresSpawn()) {
            if (playerData.isDuelArena()) {
                player.sendMessage(ChatColor.RED + "You cannot use this while in an event.");
                return;
            }

            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game != null) {
                if (game.containsPlayer(player)) {
                    player.sendMessage(ChatColor.RED + "You cannot use this while in an event.");
                    return;
                }
            }

            if (playerData.getSelectedKit() == null) {
                player.sendMessage(ChatColor.RED + "You need to have a kit equipped to use this command.");
                return;
            }

            if (requiresInventorySpace() && player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "Your inventory is full.");
                return;
            }
        }
        PlayerStatistic statistic = playerData.getStatistic();
        if (statistic.get(StatisticType.CREDITS) < credits) {
            player.sendMessage(ChatColor.RED + "You don't have enough credits.");
            return;
        }


        if (getCooldown() != null && getCooldownTime() > 0) {
            Cooldown cooldown = playerData.getCooldown(getCooldown());
            if (cooldown != null && !cooldown.hasExpired()) {
                player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + cooldown.getTimeLeft() + ChatColor.RED + " before using this again.");
                return;
            }
        }

        if (confirm) {
            new ConfirmMenu("Are you sure?", data -> {
                if (data) {
                    statistic.set(StatisticType.CREDITS, statistic.get(StatisticType.CREDITS) - credits);
                    player.sendMessage(ChatColor.YELLOW + "You've purchased a " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " for " + ChatColor.WHITE + credits + " credits" + ChatColor.YELLOW + ".");
                    purchase(player, playerData);
                } else {
                    player.sendMessage(ChatColor.RED + "Confirmation cancelled.");
                }
            }).openMenu(player);
        } else {
            purchase(player, playerData);
            statistic.set(StatisticType.CREDITS, statistic.get(StatisticType.CREDITS) - credits);
            player.sendMessage(ChatColor.YELLOW + "You've purchased a " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " for " + ChatColor.LIGHT_PURPLE + credits + " credits" + ChatColor.YELLOW + ".");
            player.closeInventory();
        }
    }

}
