package rip.thecraft.brawl.util.menu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;

import java.util.*;

@Getter
public class Menu {

    private final UUID id = UUID.randomUUID();

    private final String name;
    private List<MenuOption> options;

    private Inventory inventory;

    private Map<Integer, MenuButton> buttons = new HashMap<>();
    private List<UUID> viewers = new ArrayList<>();

    private long updateTicks = -1L;
    private BukkitTask bukkitTask;

    public Menu(String name, MenuOption... options) {
        this.name = name;
        this.options = Arrays.asList(options);
    }

    @Deprecated
    public void setUpdateTicks(Player player, long updateTicks) {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
        if (this.updateTicks != updateTicks && updateTicks >= 1L) {
            bukkitTask = Brawl.getInstance().getServer().getScheduler().runTaskTimer(Brawl.getInstance(), () -> update(player), updateTicks, updateTicks);
            this.updateTicks = updateTicks;
        }
    }

    public void update(Player player) {
        for (Map.Entry<Integer, MenuButton> entry : this.buttons.entrySet()) {
            final MenuButton button = entry.getValue();
            if (button.isDisplayingError()) {
                continue;
            }
            button.getUpdate().ifPresent(update -> this.inventory.setItem(entry.getKey(), update.update(button.getItem(player))));
        }
    }

    public void init(Player player, Map<Integer, MenuButton> buttons) {

    }

    public MenuButton addButton(int slot, MenuButton button) {
        this.buttons.put(slot, button);
        return button;
    }

    public MenuButton addButton(int x, int y, MenuButton button) {
        return addButton(getSlot(x, y), button);
    }

    public void open(Player player) {
        init(player, buttons);
        int size = size(buttons);
        if (inventory == null || size != inventory.getSize()) {
            inventory = Bukkit.createInventory(null, size(buttons), name);
        }
        Map<Integer, MenuButton> buttons = this.buttons;
        for (Map.Entry<Integer, MenuButton> entry : buttons.entrySet()) {
            MenuButton button = entry.getValue();
            this.inventory.setItem(entry.getKey(), button.getItem(player));
            button.getUpdate().ifPresent(update -> this.inventory.setItem(entry.getKey(), update.update(button.getItem(player))));
        }
        player.openInventory(inventory);
        viewers.add(player.getUniqueId());
        if (updateTicks > -1L && bukkitTask == null) {
            bukkitTask = Brawl.getInstance().getServer().getScheduler().runTaskTimer(Brawl.getInstance(), () -> update(player), updateTicks, updateTicks);
            MenuHandler.LOGGER.info("Reactivated Bukkit task for menu " + this.id.toString() + " (removed due to inactivity)");
        }
        MenuHandler.setMenu(player, this);
    }

    public int size(Map<Integer, MenuButton> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue <= highest) continue;
            highest = buttonValue;
        }
        return (int) (Math.ceil((double) (highest + 1) / 9.0) * 9.0);
    }

    public void close(Player player) {
        viewers.remove(player.getUniqueId());
        if (viewers.isEmpty() && bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
            MenuHandler.LOGGER.info("Removing task from " + this.id.toString() + " as its a unique viewer.");
        }
        MenuHandler.setMenu(player, null);
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }
}