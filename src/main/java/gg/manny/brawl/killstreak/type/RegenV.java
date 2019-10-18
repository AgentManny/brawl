package gg.manny.brawl.killstreak.type;

import gg.manny.brawl.killstreak.Killstreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RegenV extends Killstreak {

    @Override
    public int[] getKills() {
        return new int[] { 15 };
    }

    @Override
    public String getName() {
        return "Regen V";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) icon.getItemMeta();
        meta.setDisplayName(getColor() + getName());
        meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 4), true);
        icon.setItemMeta(meta);
        return icon;
    }

    @Override
    public int getAmount() {
        return 1;

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

}
