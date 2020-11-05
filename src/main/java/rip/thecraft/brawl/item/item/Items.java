package rip.thecraft.brawl.item.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.Spartan;
import rip.thecraft.spartan.util.ItemUtils;

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
        Map<Integer, String> map = Spartan.GSON.fromJson(jsonObject.get("items").getAsJsonObject(), BrawlUtil.MAP_INTEGER_STRING);
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            ItemStack item = Spartan.GSON.fromJson(entry.getValue(), ItemStack.class);
            items.add(item);
        }
        this.items = items.toArray(new ItemStack[] { });
    }

    public Items(JsonArray jsonArray) {
        List<ItemStack> items = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            items.add(element.isJsonNull() || element.isJsonNull() ? new ItemStack(Material.AIR) : Spartan.GSON.fromJson(element, ItemStack.class));
        }
        this.items = items.toArray(new ItemStack[] { });
    }

    public Items(ItemStack... items) {
        this.items = items;
    }

    public void setItem(int i, ItemStack item) {
        items[i] = item;
    }

    public JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (ItemStack itemStack : this.items) {
            jsonArray.add(itemStack == null || itemStack.getType() == Material.MUSHROOM_SOUP ? null : Spartan.GSON.toJsonTree(itemStack));
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
        boolean sword = Stream.of(ItemUtils.swords()).anyMatch(id -> id.getMaterial() == item.getType());
        boolean armor = Stream.of(ItemUtils.ArmorPart.values()).anyMatch(ap -> Stream.of(ItemUtils.armorOf(ap)).anyMatch(id -> id.getMaterial() == item.getType()));

        String itemName = ItemUtils.getName(item);

        if (sword || armor) {
            for (String part : itemName.split(" ", -1)) {

                ItemUtils.SwordType st = null;
                ItemUtils.ArmorType at = null;

                boolean matches = false;
                try {
                    if (sword) {
                        st = ItemUtils.SwordType.valueOf(part.toUpperCase());
                        matches = true;
                    } else if (armor) {

                        at = ItemUtils.ArmorType.valueOf(part.toUpperCase());
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

    public static String color(ItemUtils.SwordType at) {
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

    public static String color(ItemUtils.ArmorType at) {
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
