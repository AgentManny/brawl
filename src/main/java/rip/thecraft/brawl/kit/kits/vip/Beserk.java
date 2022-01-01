package rip.thecraft.brawl.kit.kits.vip;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import rip.thecraft.brawl.ability.abilities.Dash;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.AbstractKit;
import gg.manny.streamline.util.ItemBuilder;

public class Beserk extends AbstractKit {

    public Beserk() {
        super("Beserk", "Release a burst of energy from within gaining a brief boost of speed!");

        this.abilities.add(getAbility(Dash.class));
    }

    @Override
    public int getWeight() {
        return 6;
    }

    @Override
    public Material getIcon() {
        return Material.SUGAR;
    }

    @Override
    public Armor getArmor() {
        return new Armor(
                new ItemBuilder(Material.IRON_HELMET).create(),
                setItemColor(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                        .enchant(Enchantment.DURABILITY, 10)
                                        .create(),
                        DyeColor.CYAN
                ),
                setItemColor(new ItemBuilder(Material.LEATHER_LEGGINGS)
                                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                        .enchant(Enchantment.DURABILITY, 10)
                                        .create(),
                        DyeColor.CYAN
                ),
                new ItemBuilder(Material.IRON_BOOTS).create()
        );
    }

    @Override
    public Items getItems() {
        return new Items(new ItemBuilder(Material.IRON_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .create());
    }
}
