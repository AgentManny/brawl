package gg.manny.brawl.duelarena.menu;

import gg.manny.brawl.duelarena.command.ViewMatchInvCommand;
import gg.manny.brawl.duelarena.match.MatchSnapshot;
import gg.manny.brawl.duelarena.match.data.PostMatchData;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.Menu;
import gg.manny.pivot.util.InventoryUtil;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class PostMatchDataMenu extends Menu {

    private String playerName;

    private MatchSnapshot matchSnapshot;
    private PostMatchData postMatchData;

    public String getTitle(Player player) {
        return "Inventory of " + playerName;
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 0, y = 0;

        ItemStack[] items = InventoryUtil.fixInventoryOrder(postMatchData.getInventoryContents());
        for (int i = 0; i < 36; ++i) {
            if (items[i] != null) {
                final ItemStack itemStack = items[i];

                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                buttons.put(i, new ItemButton(itemStack));
            }
        }

        for (int i = 0; i < this.postMatchData.getArmorContents().length; i++) {
            ItemStack itemStack = this.postMatchData.getArmorContents()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(39 - i, new ItemButton(itemStack));
            }
        }

        int soup = 0;
        for(ItemStack inventoryItems : postMatchData.getInventoryContents()) {
            if (inventoryItems.getType() == Material.MUSHROOM_SOUP) {
                soup++;
            }
        }

        buttons.put(49, new ItemButton(new ItemBuilder(Material.MUSHROOM_SOUP)
                .name(ChatColor.YELLOW + "Remaining Soups: " + ChatColor.LIGHT_PURPLE + soup)
                .amount(soup)
                .create()));


        buttons.put(48, new ItemButton(new ItemBuilder(Material.SPECKLED_MELON)
                .name(ChatColor.YELLOW + "Health: " + ChatColor.LIGHT_PURPLE + ((float)(postMatchData.getHealth() / 2)) + " \u2764")
                .amount(Math.round(postMatchData.getHealth() / 2))
                .create()));

        buttons.put(50, new ItemButton(new ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.YELLOW + "Match Statistics")
                .lore(Arrays.asList(
                        CC.YELLOW + "Longest Combo: " + CC.LIGHT_PURPLE + postMatchData.getLongestCombo(),
                        CC.YELLOW + "Total Hits: " + CC.LIGHT_PURPLE + postMatchData.getTotalHits()
                ))
                .create()));

        buttons.put(45, new OpponentButton(matchSnapshot, postMatchData.getOpponent()));
        buttons.put(53, new OpponentButton(matchSnapshot, postMatchData.getOpponent()));
        return (buttons);
    }

    @AllArgsConstructor
    private class ItemButton extends Button {

        private ItemStack item;

        @Override
        public ItemStack getButtonItem(Player player) {
            return item;
        }
    }


    @AllArgsConstructor
    private class OpponentButton extends Button {

        private MatchSnapshot matchSnapshot;
        private UUID opponent;

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hotbar) {
            ViewMatchInvCommand.execute(player, matchSnapshot.getId(), opponent.toString());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name(CC.YELLOW + "View " + CC.LIGHT_PURPLE + SimpleOfflinePlayer.getNameByUuid(opponent) + CC.YELLOW + "'s inventory")
                    .create();
        }
    }


}