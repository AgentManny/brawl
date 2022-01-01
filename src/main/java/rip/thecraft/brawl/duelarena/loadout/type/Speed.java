package rip.thecraft.brawl.duelarena.loadout.type;

import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public Material getIcon() {
        return Material.SUGAR;
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
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .enchant(Enchantment.DAMAGE_ALL, 1)
                        .create()
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
          new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false)
        };
    }

    @Override
    public int getHealingAmount() {
        return 8;
    }

    @Override
    public int getWeight() {
        return 1;
    }
}
