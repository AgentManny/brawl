package gg.manny.brawl.kit.command;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.kit.type.RankType;
import gg.manny.brawl.kit.type.RarityType;
import gg.manny.brawl.util.item.item.Armor;
import gg.manny.brawl.util.item.item.Items;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KitCommand {

    private final Brawl brawl;

    @Command(names = { "kit", "k" })
    public void apply(Player player, Kit kit) {
        kit.apply(player, true, true);
    }

    @Command(names =  { "kit create", "k create" }, permission = "op")
    public void create(Player player, String name) {
        if (brawl.getKitHandler().getKit(name) != null) {
            player.sendMessage(CC.RED + "Kit " + name + " already exists.");
            return;
        }
        PlayerInventory inventory = player.getInventory();
        Kit kit = new Kit(name);
        kit.setIcon(player.getItemInHand());
        kit.setWeight(brawl.getKitHandler().getKits().size() + 1);
        kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getLeggings()));
        kit.setItems(new Items(inventory.getContents()));
        brawl.getKitHandler().registerKit(kit);
        player.sendMessage(Locale.COMMAND_KIT_CREATE.format(name));
    }

    @Command(names =  { "kit remove", "k remove" }, permission = "op")
    public void remove(Player player, Kit kit) {
        brawl.getKitHandler().unregisterKit(kit);
        player.sendMessage(Locale.COMMAND_KIT_REMOVE.format(kit.getName()));
    }

    @Command(names =  { "kit setdescription", "k setdescription" }, permission = "op")
    public void description(Player player, Kit kit, String description) {
        kit.setDescription(description.trim());
        player.sendMessage(Locale.COMMAND_KIT_DESCRIPTION.format(kit.getName(), description));
    }

    @Command(names =  { "kit ability", "k ability" }, permission = "op")
    public void ability(Player player, Kit kit, Ability ability) {
        if (kit.getAbilities().contains(ability)) {
            kit.getAbilities().remove(ability);
        } else {
            kit.getAbilities().add(ability);
        }

        player.sendMessage(Locale.COMMAND_KIT_ABILITY.format((kit.getAbilities().contains(ability) ? "Added" : "Removed"), kit.getName(), ability.getName()));
    }

    @Command(names =  { "kit setrank", "k setrank" }, permission = "op")
    public void rank(Player player, Kit kit, String rank) {
        RankType rankType = null;
        try {
            rankType = RankType.valueOf(rank.toUpperCase().replace("_", " "));
        } catch (Exception exception) {
            player.sendMessage(CC.RED + "Rank " + rank + " not found");
        } finally {
            if (rankType != null) {
                kit.setRankType(rankType);
                player.sendMessage(Locale.COMMAND_KIT_RANK.format(kit.getName(), rankType.getDisplayName()));
            } else {
                player.sendMessage(CC.RED + "Rank " + rank + " not found");
            }
        }
    }

    @Command(names =  { "kit setrarity", "k setrarity" }, permission = "op")
    public void rarity(Player player, Kit kit, String rarity) {
        RarityType rarityType = null;
        try {
            rarityType = RarityType.valueOf(rarity.toUpperCase().replace("_", " "));
        } catch (Exception exception) {
            player.sendMessage(CC.RED + "Rarity " + rarity + " not found");
        } finally {
            if (rarityType != null) {
                kit.setRarityType(rarityType);
                player.sendMessage(Locale.COMMAND_KIT_RARITY.format(kit.getName(), rarityType.getDisplayName()));
            } else {
                player.sendMessage(CC.RED + "Rarity " + rarity + " not found");
            }
        }
    }

    @Command(names =  { "kit get", "k get" }, permission = "op")
    public void get(Player player, Kit kit) {
        kit.apply(player, false, false);
        new FancyMessage(Locale.COMMAND_KIT_RETRIEVE.format(kit.getName()))
                .tooltip(CC.GRAY + "Click to update this kit")
                .command("/kit update " + kit.getName())
                .send(player);
    }

    @Command(names =  { "kit icon", "k icon" }, permission = "op")
    public void icon(Player player, Kit kit) {
        player.sendMessage(Locale.COMMAND_KIT_ICON.format(kit.getName()));
        kit.setIcon(player.getItemInHand() == null ? new ItemStack(Material.AIR) : player.getItemInHand());
    }

    @Command(names =  { "kit update", "k update" }, permission = "op")
    public void update(Player player, Kit kit) {
        PlayerInventory inventory = player.getInventory();
        kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getLeggings()));
        kit.setItems(new Items(inventory.getContents()));
        new FancyMessage(Locale.COMMAND_KIT_UPDATE.format(kit.getName()))
                .tooltip(CC.GRAY + "Click to return to spawn.")
                .command("/spawn")
                .send(player);
        brawl.getKitHandler().save();
    }

    @Command(names =  { "kit info", "k info" }, permission = "op")
    public void info(Player player, Kit kit) {
        player.sendMessage(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51));
        player.sendMessage(CC.LIGHT_PURPLE + "Kit Information of " + CC.WHITE + kit.getName());
        player.sendMessage(CC.LIGHT_PURPLE + "  Armor: " + CC.YELLOW + kit.getArmor().info());

        player.sendMessage(CC.LIGHT_PURPLE + "  Content: " + CC.YELLOW + kit.getItems().info() + CC.WHITE + " (Refill: " + kit.getRefillType().name() + ")");
        player.sendMessage(CC.LIGHT_PURPLE + "  Rarity: " + CC.YELLOW + kit.getRarityType().getDisplayName() + CC.WHITE + "[" + kit.getRarityType().name() + "] ");
        player.sendMessage(CC.LIGHT_PURPLE + "  Rank: " + CC.YELLOW + kit.getRankType().getDisplayName() + CC.WHITE + "[" + kit.getRankType().name() + "] ");
        player.sendMessage(CC.LIGHT_PURPLE + "  Abilities: " + CC.YELLOW + kit.getAbilities().stream().map(Ability::getName).collect(Collectors.joining(", ")));
        player.sendMessage(CC.LIGHT_PURPLE + "  Potion Effects:");
        kit.getPotionEffects().forEach(potionEffect -> player.sendMessage(CC.WHITE + "    - " + potionEffect.getType().getName() + " (Amplifier: " + potionEffect.getAmplifier() + ") (Infinite: " + (potionEffect.getDuration() == Integer.MAX_VALUE ? "Yes" : "No") + ")"));
        player.sendMessage(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51));
    }

}
