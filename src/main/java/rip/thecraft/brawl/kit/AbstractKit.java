package rip.thecraft.brawl.kit;

import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractKit {

    private final String name;
    private final String description;

    protected List<Ability> abilities = new ArrayList<>();

    public abstract int getWeight();

    public RankType getRank() {
        return RankType.NONE;
    }

    public abstract Material getIcon();

    public byte getData() {
        return 0;
    }

    public ItemStack getItem() {
        return new ItemBuilder(getIcon())
                .data(getData())
                .create();
    }

    public PotionEffect[] getPotions() {
        return new PotionEffect[] { };
    }

    public abstract Armor getArmor();
    public abstract Items getItems();

    public int getPrice() {
        return 0;
    }

    public Kit build() {
        Kit kit = new Kit(name);
        kit.setDescription(description);
        kit.setRankType(getRank());
        kit.setPrice(getPrice());
        kit.setAbilities(abilities);
        kit.setIcon(getItem());
        kit.setArmor(getArmor());
        kit.setItems(getItems());
        kit.setPotionEffects(Arrays.asList(getPotions()));
        kit.setWeight(getWeight());
        return kit;
    }

    public <T extends Ability> T getAbility(Class<T> clazz) {
        return Brawl.getInstance().getAbilityHandler().getAbilityByClass(clazz);
    }

    public ItemStack setItemColor(ItemStack item, DyeColor color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color.getColor());
        item.setItemMeta(meta);
        return item;
    }

}
