package rip.thecraft.brawl.kit.menu;

import gg.manny.streamline.menu.Menu;
import gg.manny.streamline.menu.MenuButton;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KitMenu extends Menu {

    public KitMenu() {
        super("Kit Selector");
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        KitHandler kitHandler = Brawl.getInstance().getKitHandler();
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        int x = 1;
        int y = 1;
        for (Kit kit : kitHandler.getKits()) {
            ItemStack item = getItem(playerData, kit);
            MenuButton button = new MenuButton(item);
            button.setClick((clicker, clickData) -> {
                ClickType clickType = clickData.getClickType();
                if (clickType == ClickType.MIDDLE) { // Preview kit details
                    // new KitPreviewMenu().open(player);
                } else {
                    if(!kit.isEnabled()){
                        button.createError(clickData, null, "This kit is currently disabled", 30L);
                        return;
                    }

                    if (playerData.hasKit(kit)) {
                        kit.apply(player, true, true);
                    } else {
                        button.createError(clickData, null, "You don't have access to use this kit.", 30L);
                    }
                }
            });
            buttons.put(getSlot(x, y), button);
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }

        MenuButton randomKit = new MenuButton(
                Material.NETHER_STAR,
                CC.LIGHT_PURPLE + CC.BOLD + "Random Kit",
                CC.GRAY + "Unsure what to play?", "",
                CC.GRAY + "Click to choose a random kit",
                CC.GRAY + "that you have access to.",
                "",
                CC.LIGHT_PURPLE + "Click to use a random kit"
        );
        randomKit.setClick((clicker, clickData) -> {
            List<Kit> kits = Brawl.getInstance().getKitHandler().getKits().stream().filter(playerData::hasKit).collect(Collectors.toList());
            Kit kit = kits.get(Brawl.RANDOM.nextInt(kits.size()));
            kit.apply(player, true, true);
        });

        int size = size(buttons) + 9;
        buttons.put(size - 5, randomKit);
    }

    private ItemStack getItem(PlayerData playerData, Kit kit) {
        ItemStack item = kit.getIcon();
        ItemMeta meta = item.getItemMeta();

        KitStatistic kitStatistic = playerData.getStatistic().get(kit);
        Map<String, Long> kitRentals = playerData.getKitRentals();
        boolean rental = kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis();
        boolean unlocked = playerData.hasKit(kit) || kit.isFreeAccess();
        boolean disabled = !kit.isEnabled();

        List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 32, false);
        if (kit.isFreeAccess()) {
            lore.add(0, " ");
            lore.add(0, CC.GREEN + ChatColor.BOLD + "- FREE KIT -");
        }
        lore.add("");

        int kills = kitStatistic.getKills();
        int deaths = kitStatistic.getDeaths();

        boolean spacer = false;
        if (kills > 0) {
            lore.add(CC.GRAY + "Kills: " + CC.WHITE + kills);
            spacer = true;
        }
        if (deaths > 0) {
            lore.add(CC.GRAY + "Deaths: " + CC.WHITE + deaths);
            spacer = true;
        }
        if (rental) {
            lore.add(CC.GRAY + "Expires in: " + CC.RED + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(kitRentals.get(kit.getName()) - System.currentTimeMillis())));
            spacer = true;
        }
        if (spacer) {
            lore.add(" ");
        }
        if (disabled || !unlocked) {
            lore.add(CC.DARK_RED + "\u2716 " + ChatColor.RED + (disabled ? "Kit currently disabled :(" : "You don't own this kit."));
        } else if (unlocked) {
            lore.add(CC.GREEN + "Click to play this kit!");
        }
        meta.setDisplayName((unlocked ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD.toString() + kit.getName());
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }
}
