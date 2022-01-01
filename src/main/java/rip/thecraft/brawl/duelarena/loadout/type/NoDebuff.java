package rip.thecraft.brawl.duelarena.loadout.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import gg.manny.streamline.util.ItemBuilder;

public class NoDebuff extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.RED;
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public Armor getArmor() {
        return Armor.of(
                new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchant(Enchantment.DURABILITY, 3)
                        .create(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchant(Enchantment.DURABILITY, 3)
                        .create(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchant(Enchantment.DURABILITY, 3)
                        .create(),
                new ItemBuilder(Material.DIAMOND_BOOTS)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .create()
        );
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 3).enchant(Enchantment.FIRE_ASPECT, 2).create(),
                new ItemBuilder(Material.ENDER_PEARL).amount(16).create(),
                new ItemBuilder(Material.COOKED_BEEF).amount(64).create(),
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.POTION).data((byte) 8259).create(),
                new ItemBuilder(Material.POTION).data((byte) 8226).create(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.POTION).data((byte) 8226).create(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.POTION).data((byte) 8226).create(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ItemBuilder(Material.POTION).data((byte) 8226).create());
    }

    @Override
    public boolean isRanked() {
        return true;
    }

    @Override
    public RefillType getRefillType() {
        return RefillType.POTION;
    }

    @Override
    public int getHealingAmount() {
        return 35;
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
