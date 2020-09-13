package rip.thecraft.brawl.item.item;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.Spartan;
import rip.thecraft.spartan.serialization.ItemStackAdapter;
import rip.thecraft.spartan.util.ItemUtils;

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

    public Armor(Material... items) {
        this.helmet = new ItemStack(items[0]);
        this.chestplate = new ItemStack(items[1]);
        this.leggings = new ItemStack(items[2]);
        this.boots = new ItemStack(items[3]);
    }

    public Armor(Player player) {
        this(player.getInventory().getArmorContents());
    }

    public Armor(ItemStack... items) {
        this.helmet = items[0];
        this.chestplate = items[1];
        this.leggings = items[2];
        this.boots = items[3];
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("helmet", this.helmet == null ? null : Spartan.GSON.toJsonTree(helmet));
        jsonObject.add("chestplate", this.chestplate == null ? null : Spartan.GSON.toJsonTree(this.chestplate));
        jsonObject.add("leggings", this.leggings == null ? null : Spartan.GSON.toJsonTree(this.leggings));
        jsonObject.add("boots", this.boots == null ? null : Spartan.GSON.toJsonTree(this.boots));
        return jsonObject;
    }

    public Armor(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isJsonNull()) {
            this.helmet = null;
            this.chestplate = null;
            this.leggings = null;
            this.boots = null;
            return;
        }

        this.helmet = BrawlUtil.has(jsonObject, "helmet") ? Spartan.GSON.fromJson(jsonObject.get("helmet"), ItemStack.class) : null;
        this.chestplate = BrawlUtil.has(jsonObject, "chestplate") ? Spartan.GSON.fromJson(jsonObject.get("chestplate"), ItemStack.class) : null;
        this.leggings = BrawlUtil.has(jsonObject, "leggings") ? Spartan.GSON.fromJson(jsonObject.get("leggings"), ItemStack.class) : null;
        this.boots = BrawlUtil.has(jsonObject, "boots") ? Spartan.GSON.fromJson(jsonObject.get("boots"), ItemStack.class) : null;
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

            String color = Stream.of(ItemUtils.ArmorType.values()).filter(at -> at.name().equalsIgnoreCase(mat)).map(Items::color).findAny().orElse("");
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
