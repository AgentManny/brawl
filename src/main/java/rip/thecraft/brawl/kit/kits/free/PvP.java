package rip.thecraft.brawl.kit.kits.free;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.AbstractKit;
import gg.manny.streamline.util.ItemBuilder;

public class PvP extends AbstractKit {

    public PvP() {
        super("PvP", "A classic, no abilities paired with iron armor!");
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public Armor getArmor() {
        return new Armor(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .enchant(Enchantment.DAMAGE_ALL, 1)
                        .enchant(Enchantment.DURABILITY, 3)
                        .create()
        );
    }
}
