package rip.thecraft.brawl.spawn.killstreak;

import rip.thecraft.brawl.player.PlayerData;
import gg.manny.streamline.util.ItemBuilder;
import rip.thecraft.server.util.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public abstract class Killstreak {

    public abstract int[] getKills();

    public String getName() {
        return getClass().getSimpleName();
    }

    public Material getType() {
        return null;
    }

    public byte getData() {
        return 0;
    }

    public int getAmount() {
        return 1;
    }

    public ItemStack getIcon() {
        if (getType() == null) return null;

        return new ItemBuilder(getType())
                .name(CC.GRAY + "\u00bb " + getColor() + CC.BOLD + getName() + CC.GRAY + " \u00ab")
                .data(getData())
                .amount(getAmount())
                .create();

    }

    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    public void onActivate(Player player, PlayerData playerData) {

    }

    public boolean isInteractable() {
        return true;
    }

    public void addItem(Player player) {
        ItemStack icon = getIcon();
        if (icon == null) return;

        if (player.getInventory().contains(icon) || player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(icon);
        } else {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null) {
                    if (item.getType() == Material.MUSHROOM_SOUP) {
                        player.getInventory().setItem(i, icon);
                        player.updateInventory();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Called upon killing a player
     * @param player Killer
     */
    public void onKill(Player player, PlayerData playerData) {
        addItem(player);
    }

}
