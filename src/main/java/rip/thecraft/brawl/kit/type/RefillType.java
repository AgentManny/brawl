package rip.thecraft.brawl.kit.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum RefillType {

    SOUP(false, new ItemStack(Material.MUSHROOM_SOUP)),
    POTION(true, new ItemStack(Material.POTION, 1, (short)16421)),
    NONE(false, new ItemStack(Material.AIR));


    private final boolean hunger;
    private final ItemStack item;

    public String getName() {
        return WordUtils.capitalizeFully(name().toLowerCase());
    }

    public static boolean isRefill(ItemStack item) {
        return getType(item) != null;
    }

    public static RefillType getType(ItemStack item) {
        if (item.isSimilar(SOUP.getItem())) {
            return SOUP;
        } else if (item.isSimilar(POTION.getItem())) {
            return POTION;
        }
        return null;
    }
}