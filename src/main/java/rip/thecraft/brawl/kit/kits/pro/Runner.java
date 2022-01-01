package rip.thecraft.brawl.kit.kits.pro;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.AbstractKit;
import gg.manny.streamline.util.ItemBuilder;

public class Runner extends AbstractKit {

    public Runner() {
        super("Runner", "");
    }

    @Override
    public int getWeight() {
        return 7;
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_BOOTS;
    }

    @Override
    public Armor getArmor() {
        return new Armor(null, null, null,
                new ItemBuilder(Material.DIAMOND_BOOTS)
                        .enchant(Enchantment.PROTECTION_FALL, 4)
                        .enchant(Enchantment.DURABILITY, 3)
                        .create()
        );
    }

    @Override
    public Items getItems() {
        return new Items(new ItemBuilder(Material.DIAMOND_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .create());
    }

    @Override
    public PotionEffect[] getPotions() {
        return new PotionEffect[] {
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2)
        };
    }
}
