package rip.thecraft.brawl.kit.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.List;
import java.util.Map;

public class KitUnlockMenu extends Menu {

    public KitUnlockMenu(Player player) {
        super("Unlock Kits");
    }

    @Override
    public void init(Player p, Map<Integer, MenuButton> buttons) {
        int x = 1;
        int y = 1;
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(p);
        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            if (kit.isFree() || playerData.hasKit(kit)) continue;

            buttons.put(getSlot(x, y), new KitUnlockButton(kit, false));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }
        addButton(3, 4,new MenuButton(Material.INK_SACK, 8, ChatColor.RED + "Go back", ChatColor.GRAY + "To Kit Selector")
                .setClick((player, click) -> {
                    player.closeInventory();
                    Bukkit.getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> new KitSelectorMenu().openMenu(player), 4L);
                }));

        MenuButton button = playerData.getUnlockingKit() == null ? new MenuButton(Material.INK_SACK, 1, ChatColor.RED + "Not unlocking kit",
                ChatColor.GRAY + "Choose a kit to start unlocking") : new KitUnlockButton(playerData.getUnlockingKit(), true);
        addButton(5, 4, button);
    }


    private class KitUnlockButton extends MenuButton {

        private final Kit kit;
        private final boolean display;

        public KitUnlockButton(Kit kit, boolean display) {
            this.kit = kit;
            this.display = display;
            if (!display) {
                    setClick((player, clickData) -> {
                        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                        if (playerData.hasKit(kit)) {
                            createError(clickData, null, "You cannot unlock kits that you already own.", 20L);
                            return;
                        }

                        if (playerData.getUnlockingKit() != kit) {
                            player.sendMessage(ChatColor.YELLOW + "You are now unlocking " + ChatColor.LIGHT_PURPLE + kit.getName() + ChatColor.YELLOW + ".");
                            player.sendMessage(ChatColor.GRAY + "Experience you accumulate will apply towards " + ChatColor.WHITE + kit.getName() + ChatColor.GRAY + "'s progression.");
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                            playerData.setUnlockingKit(kit);
                            player.closeInventory();
                        } else {
                            createError(clickData, null, "You are already unlocking this kit", 20L);
                        }
                    });
            }
        }

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 25, false);
            if (!kit.getDescription().isEmpty()) {
                lore.add("");
            }
            Kit unlockingKit = playerData.getUnlockingKit();

            KitStatistic statistic = playerData.getStatistic().get(kit);
            int exp = playerData.hasKit(kit) ? Kit.MAX_EXP_UNLOCK : statistic.getExp();
            lore.add(ChatColor.GREEN + "Progress" + ChatColor.GRAY + " (" + ChatColor.WHITE + exp + "/" + Kit.MAX_EXP_UNLOCK + ChatColor.GRAY + " EXP)");
            lore.add(BrawlUtil.getProgressBar(exp, Kit.MAX_EXP_UNLOCK, '\u25A0', 15));
            lore.add("");

            ItemBuilder builder = new ItemBuilder(kit.getIcon()).name((playerData.hasKit(kit) ? CC.RED : unlockingKit == kit ? CC.YELLOW + CC.BOLD : CC.GREEN) + kit.getName()).amount(1);
            if (!display) {
                String value;
                if (playerData.hasKit(kit)) {
                    value = CC.GREEN + "Already own this kit";
                } else if (unlockingKit != kit) {
                    value = CC.GREEN + "Start unlocking this kit";
                } else {
                    value = CC.YELLOW + "Already unlocking this kit";
                }
                lore.add(CC.GRAY + "\u00bb " + value + CC.GRAY);
                if (unlockingKit == kit) {
                    builder.enchant(Enchantment.LOOT_BONUS_BLOCKS, 1);
                }
            }
            return builder.lore(lore).create();
        }
    }
}
