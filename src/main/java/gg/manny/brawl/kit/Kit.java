package gg.manny.brawl.kit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.lang.Nullable;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.kit.type.RankType;
import gg.manny.brawl.kit.type.RarityType;
import gg.manny.brawl.kit.type.RefillType;
import gg.manny.brawl.util.item.item.Armor;
import gg.manny.brawl.util.item.item.Items;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.pivot.util.PlayerUtils;
import gg.manny.pivot.util.serialization.PotionEffectAdapter;
import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Kit implements Comparable<Kit> {

    private final String name;

    private ItemStack icon;
    private String description = "";

    private double price = 0;

    private RarityType rarityType = RarityType.NONE;
    private RankType rankType = RankType.NONE;
    private RefillType refillType = RefillType.SOUP;

    private Armor armor = new Armor();
    private Items items = new Items();

    private List<Ability> abilities = new ArrayList<>();
    private List<PotionEffect> potionEffects = new ArrayList<>();

    private List<String> sidebar = new ArrayList<>();

    private int weight = -1;

    public Kit(String name) {
        this.name = name;
    }

    public Kit(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();

        this.icon = jsonObject.has("icon") ? Pivot.GSON.fromJson(jsonObject.get("icon").getAsString(), ItemStack.class) : new ItemStack(Material.AIR);

        this.description = jsonObject.get("description").getAsString();
        this.weight = jsonObject.get("weight").getAsInt();
        this.price = jsonObject.get("price").getAsDouble();

        this.rarityType = jsonObject.has("rarityType") ? RarityType.valueOf(jsonObject.get("rarityType").getAsString()) : RarityType.NONE;
        this.rankType = jsonObject.has("rankType") ? RankType.valueOf(jsonObject.get("rankType").getAsString()) : RankType.NONE;
        this.refillType = jsonObject.has("refillType") ? RefillType.valueOf(jsonObject.get("refillType").getAsString()) : RefillType.SOUP;

        this.sidebar = Pivot.GSON.fromJson(jsonObject.get("sidebar").getAsString(), PivotUtil.LIST_STRING);

        for (JsonElement element : new JsonParser().parse(jsonObject.get("potionEffects").getAsString()).getAsJsonArray()) {
            this.potionEffects.add(PotionEffectAdapter.fromJson(element));
        }

        this.abilities = Pivot.GSON.<List<String>>fromJson(jsonObject.get("abilities").getAsString(), PivotUtil.LIST_STRING)
                .stream()
                .filter(ability -> Brawl.getInstance().getAbilityHandler().getAbilityByName(ability) != null)
                .map(Brawl.getInstance().getAbilityHandler()::getAbilityByName)
                .collect(Collectors.toList());

        if (jsonObject.has("armor") && jsonObject.get("armor") != null) {
            this.armor = new Armor(jsonObject.get("armor").getAsJsonObject());
        }

        if (jsonObject.has("items") && jsonObject.get("items") != null) {
            this.items = new Items(jsonObject.get("items").getAsJsonObject());
        }
    }

    public boolean isFree() {
        return this.price <= 0;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("icon", Pivot.GSON.toJson(this.icon));
        jsonObject.addProperty("description", this.description);
        jsonObject.addProperty("weight", this.weight);
        jsonObject.addProperty("price", this.price);
        jsonObject.addProperty("rarityType", this.rarityType.name());
        jsonObject.addProperty("rankType", this.rankType.name());
        jsonObject.addProperty("refillType", this.refillType.name());
        jsonObject.addProperty("sidebar", Pivot.GSON.toJson(this.sidebar));
        jsonObject.addProperty("potionEffects", Pivot.GSON.toJson(this.potionEffects.stream().map(PotionEffectAdapter::toJson).collect(Collectors.toList())));
        jsonObject.addProperty("abilities", Pivot.GSON.toJson(this.abilities.stream().map(Ability::getName).collect(Collectors.toList())));
        jsonObject.add("armor", this.armor == null ? null : this.armor.toJson());
        jsonObject.add("items", this.items == null ? null : this.items.toJson());

        return jsonObject;
    }

    public void apply(Player player) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        player.sendMessage(Locale.PLAYER_KIT_SELECTED.format(this.name));
        Brawl.getInstance().getPlayerDataHandler().getPlayerData(player).setSelectedKit(this);

        this.armor.apply(player);
        player.getInventory().setContents(this.items.getItems());

        this.getPotionEffects().forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        if (this.refillType.getItem().getType() != Material.AIR) {
            while (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(this.refillType.getItem());
            }
        }

        player.updateInventory();
        player.closeInventory();
    }

    @Override
    public int compareTo(@Nullable Kit kit) {
        if (kit != null) {
            return Integer.compare(this.weight, kit.getWeight());
        }
        return -1;
    }
}