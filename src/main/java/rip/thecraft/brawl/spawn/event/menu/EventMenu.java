package rip.thecraft.brawl.spawn.event.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.util.menu.Menu;
import rip.thecraft.brawl.util.menu.MenuButton;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EventMenu extends Menu {

    private final EventType eventType;

    public EventMenu(EventType eventType) {
        super("Events - " + eventType.getShortName());
        this.eventType = eventType;
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        EventHandler eventHandler = Brawl.getInstance().getEventHandler();
        int x = 1;
        int y = 1;
        Collection<Event> events = eventHandler.getEvents().get(eventType);
        for (Event event : events) {
            MenuButton button = new MenuButton(getItem(event))
                    .setClick((clicker, clickData) -> {
                        // TODO check if they got access again
                        eventHandler.start(event, clicker);
                    });
            buttons.put(getSlot(x, y), button);
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }

        MenuButton randomKit = new MenuButton(
                Material.INK_SACK, 8,
                CC.RED + "Go Back"
        ).setClick((clicker, clickData) -> new EventsMenu().open(clicker));

        int size = size(buttons) + 9;
        buttons.put(size - 5, randomKit);
    }

    private ItemStack getItem(Event event) {
        ItemStack item = new ItemStack(event.isSetup() ? Material.PAPER : Material.EMPTY_MAP, 1);
        ItemMeta meta = item.getItemMeta();

        if (!event.isSetup()) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "\u2716 Not Setup");
            List<String> setupRequirements = event.getSetupRequirements();
            if (setupRequirements != null) {
                setupRequirements.forEach(requirement -> lore.add(ChatColor.GRAY + " - " + ChatColor.RED + requirement));
            }
            meta.setLore(lore);
        }

        meta.setDisplayName((event.isSetup() ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD.toString() + event.getName());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
