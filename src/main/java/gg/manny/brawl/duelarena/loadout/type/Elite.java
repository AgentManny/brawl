package gg.manny.brawl.duelarena.loadout.type;

import gg.manny.brawl.duelarena.arena.ArenaType;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.item.item.Armor;
import gg.manny.brawl.item.item.Items;
import gg.manny.brawl.kit.type.RefillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Elite extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public Armor getArmor() {
        return Armor.of(
                Material.IRON_HELMET,
                Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS,
                Material.IRON_BOOTS
        );
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemStack(Material.DIAMOND_SWORD)
        );
    }

    @Override
    public boolean isRanked() {
        return true;
    }

    @Override
    public RefillType getRefillType() {
        return RefillType.SOUP;
    }

    @Override
    public int getHealingAmount() {
        return 8;
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
