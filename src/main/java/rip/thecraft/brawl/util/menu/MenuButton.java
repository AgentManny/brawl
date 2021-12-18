package rip.thecraft.brawl.util.menu;

import lombok.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.util.ItemBuilder;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor
@RequiredArgsConstructor
public class MenuButton {

    @NonNull private ItemStack item;

    private ButtonClick click;
    @Setter private ButtonUpdate update;

    @Getter @Setter private boolean displayingError = false;

    public ItemStack getItem(Player player) {
        return item;
    }

    public ButtonClick handleClick(Player player, ClickData clickData) {
        return null;
    }

    public MenuButton(Material material, int data, String name, String... lore) {
        this(new ItemBuilder(material).name(name).data(data).lore(lore).create());
    }

    public MenuButton(Material material, String name, String... lore) {
        this(material, 0, name, lore);
    }

    public MenuButton setClick(ButtonClick click) {
        this.click = click;
        return this;
    }

    public Optional<ButtonClick> getClick() {
        return Optional.ofNullable(this.click);
    }

    public Optional<ButtonUpdate> getUpdate() {
        return Optional.ofNullable(this.update);
    }

    public ButtonResponse setResponse(ClickData data, String title, String message) {
        return ButtonResponse.create(this, data, title, message);
    }

    public void createError(ClickData data, @Nullable String title, String message, long delayTicks) {
        if (this.displayingError) {
            return;
        }
        this.displayingError = true;
        Inventory inventory = data.getInventory();
        int slot = data.getSlot();
        ItemStack clone = inventory.getItem(slot).clone();
        ItemStack errorItem = new ItemBuilder(Material.BARRIER)
                .name((title == null ? clone.getItemMeta().getDisplayName() : ChatColor.RED.toString() + ChatColor.BOLD + title)).description(message, ChatColor.GRAY.toString()).create();
        inventory.setItem(slot, errorItem);
        Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            inventory.setItem(slot, clone);
            this.displayingError = false;
        }, delayTicks);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ButtonResponse {

        private final MenuButton button;
        private final ClickData clickData;
        private final String title;
        private final String message;

        @Setter private long delayTicks = 30L;
        @Setter private ItemStack item;

        public void apply(Player player) {
            this.button.setDisplayingError(true);
            this.clickData.inventory.setItem(this.clickData.slot, getItem(player));
            Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), this::revert, this.delayTicks);
        }

        public ItemStack getItem(Player player) {
            return (this.item == null) ? new ItemBuilder(Material.BARRIER).name(ChatColor.DARK_RED + (this.title == null ? "Error" : this.title)).description(this.message, ChatColor.GRAY.toString()).create() : this.item;
        }

        public void revert() {
            this.clickData.inventory.setItem(this.clickData.slot, this.clickData.item);
            this.button.setDisplayingError(false);
        }


        public static ButtonResponse create(MenuButton button, ClickData clickData, String title, String message) {
            return new ButtonResponse(button, clickData, title, message);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ClickData {

        private final Inventory inventory;
        private final ItemStack item;
        private final ClickType clickType;
        private final int slot;

    }

    @FunctionalInterface
    public interface ButtonUpdate {

        ItemStack update(ItemStack item);

    }

    @FunctionalInterface
    public interface ButtonClick {

        void click(Player player, ClickData clickData);

    }
}
