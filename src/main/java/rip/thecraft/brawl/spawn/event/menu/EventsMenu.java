package rip.thecraft.brawl.spawn.event.menu;

import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.menu.GameMenu;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventsMenu extends Menu {

    public EventsMenu() {
        super("Spawn Events");
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        EventHandler eventHandler = Brawl.getInstance().getEventHandler();
        int x = 1;
        int y = 1;
        EventType[] values = EventType.values();
        for (EventType type : values) {
            MenuButton button = new MenuButton(getItem(eventHandler, type));
            button.setClick((clicker, clickData) -> {
                // TODO check if they have access
                Collection<Event> events = eventHandler.getEvents().get(type);
                if (events == null || events.isEmpty()) {
                    button.createError(clickData, "No maps exist", "This event doesn't have any maps created for it. Please contact a server administrator.", 30L);
                    return;
                }

                if (events.size() == 1) { // Don't show available maps menu if there is only one map
                    eventHandler.start(events.stream().findFirst().get(), clicker);
                    return;
                }

                new EventMenu(type).open(clicker);
            });
            buttons.put(getSlot(x, y), button);
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }

        ItemStack item = new ItemBuilder(Material.FIREWORK_CHARGE)
                .color(Color.PURPLE)
                .name(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Minigames")
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .lore(ChatColor.GRAY + "Click to browse our minigame", ChatColor.GRAY + "selection.")
                .create();
        buttons.put(size(buttons) + 4, new MenuButton(item).setClick((clicker, click) -> {
            new GameMenu().open(clicker);
        }));
    }

    private ItemStack getItem(EventHandler handler, EventType type) {
        ItemStack item = new ItemStack(type.getIcon(), 1);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = ItemBuilder.wrap(type.getDescription(), CC.GRAY, 35, true);
        lore.add("");

        if (System.currentTimeMillis() - handler.getEventCooldown() <= EventHandler.EVENT_COOLDOWN_TIME) {
            lore.add(CC.YELLOW + "Cooldown: " + CC.RED + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - handler.getEventCooldown())));
            lore.add(" ");
        }

        Collection<Event> events = handler.getEvents().get(type);
        if (events == null || events.isEmpty()) {
            if (type.getRegistry() == null) {
                lore.add(CC.DARK_RED + "\u2716 " + ChatColor.RED + "This event is under development");
            } else {
                lore.add(CC.DARK_RED + "\u2716 " + ChatColor.RED + "This event doesn't have any maps");
            }
        } else {
            lore.add(CC.YELLOW + "Click to play this event!");
        }
        meta.setDisplayName(type.getColor() + ChatColor.BOLD.toString() + type.getDisplayName());
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }
}
