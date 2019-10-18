package gg.manny.brawl.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedItemStack {

    @Getter private Material type;
    @Getter private short damage;
    @Getter private Map<Integer, Integer> enchantments;
    @Getter private int amount = -1;

    public WrappedItemStack(ItemStack item) {
        this.type = item.getType();
        this.damage = item.getDurability();

        Map<Integer, Integer> map = new HashMap<>();
        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            map.put(entry.getKey().getId(), entry.getValue());
        }

        this.enchantments = map;
        this.amount = item.getAmount();
    }

    public ItemStack unwrap() {
        ItemStack item = new ItemStack(type, amount == -1 ? type.getMaxStackSize() : amount, damage);

        Map<Enchantment, Integer> map = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : enchantments.entrySet()) {
            map.put(Enchantment.getById(entry.getKey()), entry.getValue());
        }

        item.addUnsafeEnchantments(map);

        return (item);
    }

    public static WrappedItemStack[] box(ItemStack[] itemStacks) {
        List<WrappedItemStack> boxes = new ArrayList<>();

        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) {
                itemStack = new ItemStack(Material.AIR);
            }

            boxes.add(new WrappedItemStack(itemStack));
        }

        return (boxes.toArray(new WrappedItemStack[boxes.size()]));
    }

    public static ItemStack[] unbox(WrappedItemStack[] boxes) {
        List<ItemStack> items = new ArrayList<>();

        for (WrappedItemStack box : boxes) {
            items.add(box.unwrap());
        }

        return (items.toArray(new ItemStack[items.size()]));
    }

}