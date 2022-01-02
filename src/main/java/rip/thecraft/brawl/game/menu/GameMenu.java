package rip.thecraft.brawl.game.menu;

import gg.manny.streamline.menu.Menu;
import gg.manny.streamline.menu.MenuButton;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spawn.event.menu.EventsMenu;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameMenu extends Menu {

    public GameMenu() {
        super("Minigames");
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        GameHandler gameHandler = Brawl.getInstance().getGameHandler();
        int x = 1;
        int y = 1;
        GameType[] values = GameType.values();
        for (GameType type : values) {
            MenuButton button = new MenuButton(getItem(playerData, gameHandler, type));
            button.setClick((clicker, clickData) -> {
                // TODO check if they have access
                if (type.isDisabled()) {
                    button.createError(clickData, null, "This game is currently disabled", 30L);
                    return;
                }
                if (gameHandler.getMapHandler().getMaps(type).isEmpty()) {
                    button.createError(clickData, "No maps exist", "This game doesn't have any maps created for it. Please contact a server administrator.", 30L);
                    return;
                }
                if (!playerData.hasGame(type)) {
                    button.createError(clickData, null, "You don't have access to this game.", 30L);
                    return;
                }

                player.performCommand("game host " + type.name().toLowerCase());
            });
            buttons.put(getSlot(x, y), button);
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }

        ItemStack item = new ItemBuilder(Material.FIREWORK_CHARGE)
                .color(Color.YELLOW)
                .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Spawn Events")
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .lore(ChatColor.GRAY + "Click to browse our spawn", ChatColor.GRAY + "events.")
                .create();
        buttons.put(size(buttons) + 4, new MenuButton(item).setClick((clicker, click) -> {
            new EventsMenu().open(clicker);
        }));
    }

    private ItemStack getItem(PlayerData playerData, GameHandler handler, GameType type) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();

        List<String> lore = ItemBuilder.wrap(type.getDescription(), CC.GRAY, 35, true);
        lore.add("");

        long cooldown = handler.getCooldown().getOrDefault(type, 0L);
        if (System.currentTimeMillis() < cooldown) {
            lore.add(CC.YELLOW + "Cooldown: " + CC.RED + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(cooldown - System.currentTimeMillis())));
            lore.add(" ");
        }

        boolean disabled = type.isDisabled();
        boolean noMaps = Brawl.getInstance().getGameHandler().getMapHandler().getMaps(type).isEmpty();
        boolean access = playerData.hasGame(type);
        String value = null;
        if (disabled) {
            value = "This game is currently disabled";
        } else if (noMaps) {
            value = "This game doesn't have any maps";
        } else if (access) {
            value = "Exclusive to " + type.getRankType().getDisplayName() + ChatColor.RED + " rank";
        }
        if (value != null) {
            lore.add(CC.DARK_RED + "\u2716 " + ChatColor.RED + value);
        } else {
            lore.add(CC.YELLOW + "Click to play this game!");
        }
        meta.setDisplayName(type.getColor() + ChatColor.BOLD.toString() + type.getName());
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }
}
