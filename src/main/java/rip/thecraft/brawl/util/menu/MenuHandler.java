package rip.thecraft.brawl.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MenuHandler implements Listener {

    public static Logger LOGGER = Logger.getLogger("Menu");
    private static final Map<UUID, Menu> openedMenus = new ConcurrentHashMap<>();

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new MenuHandler(), plugin);
    }

    public static void setMenu(Player player, Menu menu) {
        if (menu == null) {
            openedMenus.remove(player.getUniqueId());
            // LOGGER.log(Level.INFO, "Removed menu from " + player.getName() + ".");
            return;
        }
        openedMenus.put(player.getUniqueId(), menu);
        // LOGGER.log(Level.INFO, "Set menu for " + player.getName() + " to: " + menu.getId().toString() + " (" + menu.getName() + ")");
    }

    public static Optional<Menu> getMenu(Player player) {
        return Optional.ofNullable(openedMenus.get(player.getUniqueId()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = event.getActor();
        getMenu(player).ifPresent(menu -> menu.close(player));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = event.getActor();
        int slot = event.getSlot();
        getMenu(player).ifPresent(menu -> {
            if (menu.getButtons().containsKey(slot)) {
                menu.getButtons().get(event.getSlot()).getClick().ifPresent(click ->
                        click.click(player, new MenuButton.ClickData(event.getClickedInventory(), event.getCurrentItem(), event.getClick(), slot))
                );
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        });
        this.processMenu(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryDragEvent event) {
        Player player = event.getActor();
        processMenu(event);
    }

    public void processMenu(InventoryInteractEvent event) {
        Player player = event.getActor().getPlayer();
        getMenu(player).ifPresent(menu -> {
            if (!menu.getOptions().contains(MenuOption.ALLOW_INTERACT)) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        });
    }
}