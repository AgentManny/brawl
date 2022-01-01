package rip.thecraft.brawl.duelarena.loadout.type;

import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Tank extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getIcon() {
        return Material.GOLDEN_APPLE;
    }

    @Override
    public Armor getArmor() {
        return Armor.of(
                getItem(Material.DIAMOND_HELMET),
                getItem(Material.DIAMOND_CHESTPLATE),
                getItem(Material.DIAMOND_LEGGINGS),
                getItem(Material.DIAMOND_BOOTS)
        );
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .enchant(Enchantment.DAMAGE_ALL, 5)
                        .create(),
                new ItemStack(Material.GOLDEN_APPLE, 5)
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
    public PotionEffect[] getEffects() {
        return new PotionEffect[] {
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false),
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false)
        };
    }

    @Override
    public int getHealingAmount() {
        return 7;
    }

    @Override
    public int getWeight() {
        return 4;
    }

    private ItemStack getItem(Material type) {
        ItemBuilder builder = new ItemBuilder(type)
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .enchant(Enchantment.DURABILITY, 3);

        if (type == Material.DIAMOND_BOOTS) {
            builder.enchant(Enchantment.PROTECTION_FALL, 4);
        }

        return builder.create();
    }
}
