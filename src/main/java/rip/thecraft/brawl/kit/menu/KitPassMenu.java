package rip.thecraft.brawl.kit.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.server.util.chatcolor.CC;
import gg.manny.streamline.util.ItemBuilder;

import java.util.List;
import java.util.Map;

public class KitPassMenu extends Menu {

    public KitPassMenu() {
        super("Kit Passes");
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        int x = 1;
        int y = 1;
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        for (Kit kit : Brawl.getInstance().getKitHandler().getKits()) {
            if (kit.isFree() || playerData.hasKit(kit)) continue;

            buttons.put(getSlot(x, y), new KitPassButton(kit));
            if (x++ >= 7) {
                x = 1;

                y++;
            }
        }
        addButton(4, 4,new MenuButton(Material.INK_SACK, 8, ChatColor.RED + "Go back", ChatColor.GRAY + "To Kit Selector")
                .setClick((player2, click) -> {
                    player2.closeInventory();
                    Bukkit.getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> new KitSelectorMenu().openMenu(player2), 4L);
                }));

    }


    private class KitPassButton extends MenuButton {

        private final Kit kit;

        public KitPassButton(Kit kit) {
            this.kit = kit;
            setClick((player, clickData) -> {
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                if (playerData.hasKit(kit)) {
                    createError(clickData, null, "You cannot use a pass on kits that you already own.", 20L);
                    return;
                }

                if (playerData.getKitPasses() == 0) {
                    createError(clickData, null, "You don't have any kit passes.", 20L);
                    return;
                }

                playerData.setKitPasses(playerData.getKitPasses() - 1);
                KitStatistic kitStatistic = playerData.getStatistic().get(kit);
                kitStatistic.setTrialPass(kitStatistic.getTrialPass() + 1);
                player.sendMessage(ChatColor.YELLOW + "You can now use " + ChatColor.LIGHT_PURPLE + kit.getName() + ChatColor.YELLOW + ".");
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                player.closeInventory();
            });
        }

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 25, false);
            if (!kit.getDescription().isEmpty()) {
                lore.add("");
            }
            KitStatistic statistic = playerData.getStatistic().get(kit);
            lore.add(ChatColor.GRAY + "Passes: " + ChatColor.WHITE + statistic.getTrialPass() + ChatColor.GRAY + " (Global: " + playerData.getKitPasses() + ")");
            lore.add("");
            ItemBuilder builder = new ItemBuilder(kit.getIcon()).name((playerData.hasKit(kit) ? CC.RED : CC.GREEN) + kit.getName()).amount(1);
            String value;
            if (playerData.hasKit(kit)) {
                value = CC.RED + "Already own this kit";
            } else {
                value = CC.GREEN + "Rent this kit for 1 hour";
            }
            lore.add(CC.GRAY + "\u00bb " + value + CC.GRAY);
            return builder.lore(lore).create();
        }
    }
}
