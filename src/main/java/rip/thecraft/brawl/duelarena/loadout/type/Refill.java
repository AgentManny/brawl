package rip.thecraft.brawl.duelarena.loadout.type;

import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Refill extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public Material getIcon() {
        return Material.MUSHROOM_SOUP;
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
    public boolean isRanked() {
        return true;
    }

    @Override
    public Items getItems() {
        return new Items(
                new ItemBuilder(Material.DIAMOND_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .create()
        );
    }

    @Override
    public RefillType getRefillType() {
        return RefillType.SOUP;
    }

    @Override
    public int getHealingAmount() {
        return 35;
    }

    @Override
    public PotionEffect[] getEffects() {
        return new PotionEffect[] {
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false)
        };
    }

    @Override
    public int getWeight() {
        return 2;
    }
}
