package gg.manny.brawl.kit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.lang.Nullable;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.item.item.Armor;
import gg.manny.brawl.item.item.Items;
import gg.manny.brawl.kit.type.RankType;
import gg.manny.brawl.kit.type.RefillType;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.pivot.serialization.ItemStackAdapter;
import gg.manny.pivot.serialization.PotionEffectAdapter;
import gg.manny.pivot.util.PlayerUtils;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class Kit implements Listener, Comparable<Kit> {

    private final String name;

    private ItemStack icon;
    private String description = "";

    private double price = 0;

    private RankType rankType = RankType.NONE;
    private RefillType refillType = RefillType.SOUP;

    private Armor armor = new Armor();
    private Items items = new Items();

    private List<Ability> abilities = new ArrayList<>();
    private List<PotionEffect> potionEffects = new ArrayList<>();

    private int weight = -1;

    public Kit(String name) {
        this.name = name;
    }

    public Kit(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();

        this.icon = BrawlUtil.has(jsonObject, "icon") ? ItemStackAdapter.deserialize(jsonObject.get("icon")) : new ItemStack(Material.AIR);

        this.description = jsonObject.get("description").getAsString();
        this.weight = jsonObject.get("weight").getAsInt();
        this.price = jsonObject.get("price").getAsDouble();

        this.rankType = jsonObject.has("rankType") ? RankType.valueOf(jsonObject.get("rankType").getAsString()) : RankType.NONE;
        this.refillType = jsonObject.has("refillType") ? RefillType.valueOf(jsonObject.get("refillType").getAsString()) : RefillType.SOUP;
        jsonObject.get("potionEffects").getAsJsonArray().forEach(element -> this.potionEffects.add(PotionEffectAdapter.fromJson(element)));

        if (jsonObject.has("abilities") && !jsonObject.get("abilities").isJsonNull()) {
            jsonObject.get("abilities").getAsJsonArray().forEach(element -> {
                Ability ability = Brawl.getInstance().getAbilityHandler().getAbilityByName(element.getAsString());
                if (ability == null) {
                    Brawl.getInstance().getLogger().severe("[Kit] " + this.name + " failed to register ability " + element.getAsString() + " (Not found!)");
                } else {
                    this.abilities.add(ability);
                }
            });
        }

        this.armor = new Armor(jsonObject.get("armor").getAsJsonObject());
        this.items = new Items(jsonObject.get("items").getAsJsonArray());
    }

    public boolean isFree() {
        return this.price <= 0;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.name);
        jsonObject.add("icon", ItemStackAdapter.serialize(this.icon));
        jsonObject.addProperty("description", this.description);
        jsonObject.addProperty("weight", this.weight);
        jsonObject.addProperty("price", this.price);
        jsonObject.addProperty("rankType", this.rankType.name());
        jsonObject.addProperty("refillType", this.refillType.name());

        JsonArray potionEffectsArray = new JsonArray();
        for (PotionEffect potionEffect : this.potionEffects) {
            potionEffectsArray.add(PotionEffectAdapter.toJson(potionEffect));
        }
        jsonObject.add("potionEffects", potionEffectsArray);

        JsonArray abilitiesArray = new JsonArray();
        for (String entry : this.abilities.stream().map(Ability::getName).collect(Collectors.toList())) {
            abilitiesArray.add(new JsonPrimitive(entry));
        }
        jsonObject.add("abilities", abilitiesArray);

//        jsonObject.addProperty("potionEffects", Pivot.GSON.toJson(this.potionEffects.stream().map(PotionEffectAdapter::toJson).collect(Collectors.toList())));
//        jsonObject.addProperty("type", Pivot.GSON.toJson(this.type.stream().map(Ability::getName).collect(Collectors.toList())));
        jsonObject.add("armor", this.armor == null ? null : this.armor.toJson());
        jsonObject.add("items", this.items == null ? null : this.items.toJson());

        return jsonObject;
    }

    public void apply(Player player, boolean updateProfile, boolean addRefill) {
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        if (updateProfile) {
            player.sendMessage(ChatColor.YELLOW + "You have chosen the " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " kit.");
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            playerData.setSelectedKit(this);
            playerData.getStatistic().get(this).addUses();
        }

        this.armor.apply(player);
        player.getInventory().setContents(this.items.getItems());

        this.abilities.stream().map(Ability::getIcon).filter(Objects::nonNull).forEach(player.getInventory()::addItem);
        this.abilities.forEach(ability -> ability.onApply(player));
        this.potionEffects.forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        if (addRefill) {
            if (this.refillType.getItem().getType() != Material.AIR) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(this.refillType.getItem());
                }
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