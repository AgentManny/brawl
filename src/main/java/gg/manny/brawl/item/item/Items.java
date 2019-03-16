package gg.manny.brawl.item.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.inventory.ItemUtil;
import gg.manny.pivot.util.serialization.ItemStackAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Items {

    @Getter
    private ItemStack[] items;

    public Items() {
        this.items = new ItemStack[] { };
    }

    @Deprecated
    public Items(JsonObject jsonObject) {
        Map<Integer, String> map = Pivot.GSON.fromJson(jsonObject.get("items").getAsJsonObject(), BrawlUtil.MAP_INTEGER_STRING);
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            ItemStack item = Pivot.GSON.fromJson(entry.getValue(), ItemStack.class);
            items.add(item);
        }
        this.items = items.toArray(new ItemStack[] { });
    }

    public Items(JsonArray jsonArray) {
        List<ItemStack> items = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            items.add(ItemStackAdapter.deserialize(element));
        }
        this.items = items.toArray(new ItemStack[] { });
    }


    public Items(ItemStack... items) {
        this.items = items;
    }

    public JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (ItemStack itemStack : this.items) {
            jsonArray.add(ItemStackAdapter.serialize(itemStack));
        }
        return jsonArray;
    }


    public String info() {

        List<String> items = new ArrayList<>();

        for (ItemStack it : this.items) {

            if (it != null) {
                items.add(friendly(it));
            }
        }

        return items.stream().collect(Collectors.joining(CC.GRAY + ", " + CC.WHITE));
    }

    public static String friendly(ItemStack item) {
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        boolean sword = Stream.of(ItemUtil.swords()).anyMatch(id -> id.getMaterial() == item.getType());
        boolean armor = Stream.of(ItemUtil.ArmorPart.values()).anyMatch(ap -> Stream.of(ItemUtil.armorOf(ap)).anyMatch(id -> id.getMaterial() == item.getType()));

        String itemName = ItemUtil.getName(item);

        if (sword || armor) {
            for (String part : itemName.split(" ", -1)) {

                ItemUtil.SwordType st = null;
                ItemUtil.ArmorType at = null;

                boolean matches = false;
                try {
                    if (sword) {
                        st = ItemUtil.SwordType.valueOf(part.toUpperCase());
                        matches = true;
                    } else if (armor) {

                        at = ItemUtil.ArmorType.valueOf(part.toUpperCase());
                        matches = true;
                    }
                } catch (IllegalArgumentException ex) {
                }

                if (matches) {

                    int total = itemName.length();
                    String color = (sword ? color(st) : color(at));

                    itemName = color + itemName;

                    for (int i = 0; i < total; i++) {

                        if (itemName.charAt(i) == ' ') {

                            itemName = itemName.substring(0, i + 1) + color + itemName.substring(i + 1);
                            total += 2;
                            i += 1;

                        }
                    }

                    break;
                }
            }
        }

        return itemName;
    }

    public static String color(ItemUtil.SwordType at) {
        switch (at) {
            case DIAMOND:
                return CC.AQUA;
            case GOLD:
                return CC.GOLD;
            case IRON:
                return CC.GREEN;
            case STONE:
                return CC.DARK_GRAY;
            default:
                return CC.WHITE;
        }
    }

    public static String color(ItemUtil.ArmorType at) {
        switch (at) {
            case DIAMOND:
                return CC.AQUA;
            case GOLD:
                return CC.GOLD;
            case IRON:
                return CC.GREEN;
            case LEATHER:
                return CC.DARK_GRAY;
            default:
                return "";
        }
    }
}