package rip.thecraft.brawl.kit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.lang.Nullable;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.spartan.Spartan;
import rip.thecraft.spartan.serialization.ItemStackAdapter;
import rip.thecraft.spartan.serialization.PotionEffectAdapter;

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

        ItemStack icon = BrawlUtil.has(jsonObject, "icon") ? ItemStackAdapter.deserialize(jsonObject.get("icon")) : null;
        this.icon = icon == null ? new ItemStack(Material.STONE) : icon;

        this.description = jsonObject.get("description").getAsString();
        this.weight = jsonObject.get("weight").getAsInt();
        this.price = jsonObject.get("price").getAsDouble();

        this.rankType = jsonObject.has("rankType") ? RankType.valueOf(jsonObject.get("rankType").getAsString()) : RankType.NONE;
        jsonObject.get("potionEffects").getAsJsonArray().forEach(element -> this.potionEffects.add(PotionEffectAdapter.fromJson(element)));

        JsonElement abilities = jsonObject.get("abilities");
        if (abilities != null && !abilities.isJsonNull() && abilities.isJsonArray()) {
            abilities.getAsJsonArray().forEach(element -> {
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

        jsonObject.addProperty("potionEffects", Spartan.GSON.toJson(this.potionEffects.stream().map(PotionEffectAdapter::toJson).collect(Collectors.toList())));
//        jsonObject.addProperty("type", Spartan.GSON.toJson(this.type.stream().map(Ability::getName).collect(Collectors.toList())));
        jsonObject.add("armor", this.armor == null ? null : this.armor.toJson());
        jsonObject.add("items", this.items == null ? null : this.items.toJson());

        return jsonObject;
    }

    public void apply(Player player, boolean updateProfile, boolean addRefill) {
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (updateProfile) {
            player.sendMessage(ChatColor.YELLOW + "You have chosen the " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " kit.");
            playerData.setSelectedKit(this);
            playerData.getStatistic().get(this).addUses();
        }

        this.armor.apply(player);
        player.getInventory().setContents(this.items.getItems());

        this.abilities.stream().map(Ability::getIcon).filter(Objects::nonNull).forEach(player.getInventory()::addItem);
        this.abilities.forEach(ability -> ability.onApply(player));
        this.potionEffects.forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        if (addRefill) {
            ItemStack item = playerData.getRefillType().getItem();
            if (item.getType() != Material.AIR) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
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