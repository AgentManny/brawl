package gg.manny.brawl.kit.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum  RefillType {

    SOUP(false, new ItemStack(Material.MUSHROOM_SOUP)),
    POTION(true, new ItemStack(Material.POTION, 1, (short)16421)),
    NONE(false, new ItemStack(Material.AIR));

    private final boolean hunger;
    private final ItemStack item;

}