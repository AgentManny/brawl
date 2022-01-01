package rip.thecraft.brawl.kit.kits.free;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.AbstractKit;
import gg.manny.streamline.util.ItemBuilder;

public class Archer extends AbstractKit {

    public Archer() {
        super("Archer", "A bow that packs a punch. Damage players from a distance!");
    }

    @Override
    public int getWeight() {
        return 2;
    }

    @Override
    public Material getIcon() {
        return Material.BOW;
    }

    @Override
    public Armor getArmor() {


        return new Armor(
                new ItemBuilder(Material.LEATHER_HELMET)
                        .enchant(Enchantment.DURABILITY, 100)
                        .create(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .create(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .create(),
                new ItemBuilder(Material.LEATHER_BOOTS)
                        .enchant(Enchantment.DURABILITY, 100)
                        .create()
        );
    }

    @Override
    public Items getItems() {
        Items items = new Items(
                new ItemBuilder(Material.STONE_SWORD)
                        .enchant(Enchantment.DAMAGE_ALL, 1)
                        .enchant(Enchantment.DURABILITY, 10)
                        .create(),
                new ItemBuilder(Material.BOW)
                        .enchant(Enchantment.ARROW_DAMAGE, 3)
                        .enchant(Enchantment.ARROW_KNOCKBACK, 1)
                        .enchant(Enchantment.ARROW_INFINITE, 1)
                        .create()
        );
        items.setItem(27, new ItemStack(Material.ARROW));
        return items;
    }
}
