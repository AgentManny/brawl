package rip.thecraft.brawl.duelarena.menu;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.duelarena.command.ViewMatchInvCommand;
import rip.thecraft.brawl.duelarena.match.MatchSnapshot;
import rip.thecraft.brawl.duelarena.match.data.PostMatchData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.*;

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

        int x = 0;
        int y = 0;

        List<ItemStack> targetInv = new ArrayList<>(Arrays.asList(postMatchData.getInventoryContents()));

        // we want the hotbar (the first 9 items) to be at the bottom (end),
        // not the top (start) of the list, so we rotate them.
        for (int i = 0; i < 9; i++) {
            targetInv.add(targetInv.remove(0));
        }

        for (ItemStack inventoryItem : targetInv) {
            buttons.put(getSlot(x, y), Button.fromItem(inventoryItem));

            if (x++ > 7) {
                x = 0;
                y++;
            }
        }

        x = 3; // start armor backwards, helm first

        for (ItemStack armorItem : postMatchData.getArmorContents()) {
            buttons.put(getSlot(x--, y), Button.fromItem(armorItem));
        }

        int soup = 0;
        for(ItemStack inventoryItems : postMatchData.getInventoryContents()) {
            if (inventoryItems.getType() == Material.MUSHROOM_SOUP) {
                soup++;
            }
        }

        buttons.put(49, Button.fromItem(new ItemBuilder(Material.MUSHROOM_SOUP)
                .name(ChatColor.YELLOW + "Remaining Soups: " + ChatColor.LIGHT_PURPLE + soup)
                .amount(soup)
                .create()));


        buttons.put(48, Button.fromItem(new ItemBuilder(Material.SPECKLED_MELON)
                .name(ChatColor.YELLOW + "Health: " + ChatColor.LIGHT_PURPLE + ((float)(postMatchData.getHealth() / 2)) + " \u2764")
                .amount(Math.round(postMatchData.getHealth() / 2))
                .create()));

        buttons.put(50, Button.fromItem(new ItemBuilder(Material.DIAMOND_SWORD)
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
    private class OpponentButton extends Button {

        private MatchSnapshot matchSnapshot;
        private UUID opponent;

        @Override
        public String getName(Player player) {
            return CC.YELLOW + "View " + CC.LIGHT_PURPLE + MUUIDCache.name(opponent) + CC.YELLOW + "'s inventory";
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.PAPER;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            ViewMatchInvCommand.execute(player, matchSnapshot.getId(), opponent.toString());
        }
    }


}