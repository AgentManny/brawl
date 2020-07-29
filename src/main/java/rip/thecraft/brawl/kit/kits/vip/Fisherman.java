package rip.thecraft.brawl.kit.kits.vip;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.AbstractKit;
import rip.thecraft.spartan.util.ItemBuilder;

public class Fisherman extends AbstractKit {

    public Fisherman() {
        super("Fisherman", "Special fishing rod that teleports players to you!");

        this.abilities.add(getAbility(rip.thecraft.brawl.ability.abilities.classic.Fisherman.class));
    }

    @Override
    public int getWeight() {
        return 5;
    }

    @Override
    public Material getIcon() {
        return Material.FISHING_ROD;
    }

    @Override
    public Armor getArmor() {
        return new Armor(
                new ItemBuilder(Material.GOLD_HELMET)
                        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchant(Enchantment.WATER_WORKER, 1)
                        .enchant(Enchantment.DURABILITY, 10)
                        .create(),
                new ItemBuilder(Material.IRON_HELMET).create(),
                new ItemBuilder(Material.IRON_CHESTPLATE).create(),
                new ItemBuilder(Material.IRON_BOOTS)
                        .enchant(Enchantment.PROTECTION_FALL, 4)
                        .create()
        );
    }

    @Override
    public Items getItems() {
        return new Items(new ItemBuilder(Material.DIAMOND_SWORD).create());
    }
}
