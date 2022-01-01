package rip.thecraft.brawl.duelarena.loadout.type;

import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HG extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.RED;
    }

    @Override
    public Material getIcon() {
        return Material.RED_MUSHROOM;
    }

    @Override
    public Armor getArmor() {
        return Armor.of(
                Material.AIR,
                Material.AIR,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        );
    }

    @Override
    public boolean isRanked() {
        return true;
    }

    @Override
    public Items getItems() {
        ItemStack[] items = new ItemStack[36];
        items[0] = new ItemStack(Material.STONE_SWORD);
        items[15] = new ItemStack(Material.BOWL, 64);
        items[16] = new ItemStack(Material.RED_MUSHROOM , 64);
        items[17] = new ItemStack(Material.BROWN_MUSHROOM, 64);

        return new Items(items);
    }

    @Override
    public RefillType getRefillType() {
        return RefillType.SOUP;
    }

    @Override
    public int getHealingAmount() {
        return 40;
    }

    @Override
    public int getWeight() {
        return 3;
    }
}
