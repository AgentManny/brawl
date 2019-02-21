package gg.manny.brawl.item.item;

import com.google.gson.JsonObject;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.util.inventory.ItemUtil;
import gg.manny.pivot.util.serialization.ItemStackAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Data
public class Armor {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public Armor() {
        this.helmet = null;
        this.chestplate = null;
        this.leggings = null;
        this.boots = null;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("helmet", this.helmet == null ? null : ItemStackAdapter.serialize(this.helmet));
        jsonObject.add("chestplate", this.chestplate == null ? null : ItemStackAdapter.serialize(this.chestplate));
        jsonObject.add("leggings", this.leggings == null ? null : ItemStackAdapter.serialize(this.leggings));
        jsonObject.add("boots", this.boots == null ? null : ItemStackAdapter.serialize(this.boots));
        return jsonObject;
    }

    public Armor(JsonObject jsonObject) {
        this.helmet = BrawlUtil.has(jsonObject, "helmet") ? ItemStackAdapter.deserialize(jsonObject.get("helmet")) : null;
        this.chestplate = BrawlUtil.has(jsonObject, "chestplate") ? ItemStackAdapter.deserialize(jsonObject.get("chestplate")) : null;
        this.leggings = BrawlUtil.has(jsonObject, "leggings") ? ItemStackAdapter.deserialize(jsonObject.get("leggings")) : null;
        this.boots = BrawlUtil.has(jsonObject, "boots") ? ItemStackAdapter.deserialize(jsonObject.get("boots")) : null;
    }

    public String info() {

        List<String> items = new ArrayList<>();

        Material type = null;

        boolean same = true;

        for (ItemStack it : new ItemStack[]{helmet, chestplate, leggings, boots}) {
            if (it != null) {

                if (type == null) {
                    type = it.getType();
                }

                if (type.name().contains("_") && it.getType().name().contains("_")) {
                    if (!type.name().split("_")[0].equalsIgnoreCase(it.getType().name().split("_")[0])) {
                        same = false;

                    }
                } else {
                    same = false;
                }

                items.add(Items.friendly(it));


            }
        }

        if (same) {
            String mat = type.name().split("_")[0];

            String color = Stream.of(ItemUtil.ArmorType.values()).filter(at -> at.name().equalsIgnoreCase(mat)).map(Items::color).findAny().orElse("");
            return color + StringUtils.capitalize(mat.toLowerCase()) + " Set";
        }

        return items.stream().collect(Collectors.joining(CC.GRAY + ", " + CC.WHITE));
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();

        pi.setHelmet(helmet);
        pi.setChestplate(chestplate);
        pi.setLeggings(leggings);
        pi.setBoots(boots);
    }

    public void mod(int ordinal, ItemStack it) {
        switch (ordinal) {
            case 0:
                setHelmet(it);
                break;
            case 1:
                setChestplate(it);
                break;
            case 2:
                setLeggings(it);
                break;
            case 3:
                setBoots(it);
                break;
            default:
        }

    }

    public static Armor of(Material... materials) {
        Armor ad = new Armor(null, null, null, null);
        for (int i = 0; i < materials.length; i++) {
            ad.mod(i, new ItemStack(materials[i]));
        }

        return ad;
    }

    public static Armor of(ItemStack... items) {
        Armor ad = new Armor(null, null, null, null);
        for (int i = 0; i < items.length; i++) {
            ad.mod(i, items[i]);
        }

        return ad;
    }
}
