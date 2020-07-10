package rip.thecraft.brawl.kit.command;

import com.google.common.base.Strings;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.menu.KitEditMenu;
import rip.thecraft.brawl.kit.editor.menu.KitEditorMenu;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class KitManageCommand {

    @Command(names = { "kit edit", "k edit" }, permission = "op")
    public static void edit(Player player, Kit kit) {
        player.sendMessage(CC.GREEN + "Updating kit " + kit.getName() + "...");
        new KitEditMenu(kit).openMenu(player);
    }

    @Command(names = { "kit editor", "k editor" }, permission = "op")
    public static void editor(Player player) {
        new KitEditorMenu().openMenu(player);
    }


    @Command(names = {"kit manage create", "k manage create"}, permission = "op")
    public static void create(Player player, String name) {
        if (Brawl.getInstance().getKitHandler().getKit(name) != null) {
            player.sendMessage(CC.RED + "Kit " + name + " already exists.");
            return;
        }
        PlayerInventory inventory = player.getInventory();
        Kit kit = new Kit(name);
        kit.setIcon(player.getItemInHand());
        kit.setWeight(Brawl.getInstance().getKitHandler().getKits().size() + 1);
        kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()));
        kit.setItems(new Items(inventory.getContents()));
        Brawl.getInstance().getKitHandler().registerKit(kit);

        player.sendMessage(ChatColor.GREEN + "Created kit " + name + ".");
    }

    @Command(names = {"kit manage remove", "k manage remove"}, permission = "op")
    public static void remove(Player player, Kit kit) {
        Brawl.getInstance().getKitHandler().unregisterKit(kit);
        player.sendMessage(ChatColor.RED + "Removed kit " + kit.getName() + ".");
    }

    @Command(names = {"kit manage setweight", "k manage setweight"}, permission = "op")
    public static void setWeight(Player player, Kit kit, int weight) {
        kit.setWeight(weight);
        Brawl.getInstance().getKitHandler().getKits().sort(Kit::compareTo);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " weight set to " + weight);
    }


    @Command(names = {"kit manage setprice", "k manage setprice"}, permission = "op")
    public static void setPrice(Player player, Kit kit, int price) {
        kit.setPrice(price);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " price set to " + price);
    }

    @Command(names = {"kit manage setdescription", "k manage setdescription"}, permission = "op")
    public static void description(Player player, Kit kit, String description) {
        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + " from \"" + kit.getDescription() + "\" to \"" + description.trim() + "\".");
        kit.setDescription(description.trim());
    }

    @Command(names = {"kit manage setrank", "k manage setrank"}, permission = "op")
    public static void setRank(Player player, Kit kit, String rank) {
        RankType rankType = null;
        try {
            rankType = RankType.valueOf(rank.toUpperCase().replace("_", " "));
        } catch (Exception exception) {
            player.sendMessage(CC.RED + "Rank " + rank + " not found");
            return;
        }
        kit.setRankType(rankType);
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + " is now restricted to " + rankType.getDisplayName() + ChatColor.GREEN + ".");
    }

    @Command(names = {"kit manage seticon", "k manage seticon"}, permission = "op")
    public static void icon(Player player, Kit kit) {
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + " icon is now the item you're holding.");
        kit.setIcon(player.getItemInHand() == null ? new ItemStack(Material.AIR) : player.getItemInHand());
    }

    @Command(names = {"kit manage ability", "k manage ability"}, permission = "op")
    public static void ability(Player player, Kit kit, Ability ability) {
        if (kit.getAbilities().contains(ability)) {
            kit.getAbilities().remove(ability);
        } else {
            kit.getAbilities().add(ability);
        }
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + (kit.getAbilities().contains(ability) ? "now has" : "no longer has") + " ability " + ChatColor.BOLD + ability.getName() + ChatColor.GREEN + ".");
    }

    @Command(names = {"kit manage load", "k manage load"}, permission = "op")
    public static void load(Player player, Kit kit) {
        kit.apply(player, false, false);
        new FancyMessage(ChatColor.GREEN + "You've been given " + kit.getName() + ChatColor.GREEN + ". Click Here to update this kit.")
                .tooltip(CC.GRAY + "Click to update this kit")
                .command("/kit update " + kit.getName())
                .send(player);
    }

    @Command(names = {"kit manage save", "k manage save"}, permission = "op")
    public static void save(Player player) {
        Brawl.getInstance().getKitHandler().save();
        player.sendMessage(CC.GREEN + "Saved all kits");
    }

    @Command(names = {"kit manage update", "k manage update"}, permission = "op")
    public static void update(Player player, Kit kit) {
        PlayerInventory inventory = player.getInventory();
        kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()));
        kit.setItems(new Items(inventory.getContents()));
        new FancyMessage(ChatColor.GREEN + "Updated kit. Click Here to return back to spawn.")
                .tooltip(CC.GRAY + "Click to return to spawn.")
                .command("/spawn")
                .send(player);
        Brawl.getInstance().getKitHandler().save();
    }

    @Command(names = {"kit manage info", "k manage info"}, permission = "op")
    public static void info(Player player, Kit kit) {
        player.sendMessage(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51));
        player.sendMessage(CC.LIGHT_PURPLE + "Kit Information of " + CC.WHITE + kit.getName());
        player.sendMessage(CC.LIGHT_PURPLE + "  Armor: " + CC.YELLOW + kit.getArmor().info());

        player.sendMessage(CC.LIGHT_PURPLE + "  Content: " + CC.YELLOW + kit.getItems().info());
        player.sendMessage(CC.LIGHT_PURPLE + "  Rank: " + CC.YELLOW + kit.getRankType().getDisplayName() + CC.WHITE + "[" + kit.getRankType().name() + "] ");
        player.sendMessage(CC.LIGHT_PURPLE + "  Abilities: " + CC.YELLOW + kit.getAbilities().stream().map(Ability::getName).collect(Collectors.joining(", ")));
        player.sendMessage(CC.LIGHT_PURPLE + "  Potion Effects:");
        kit.getPotionEffects().forEach(potionEffect -> player.sendMessage(CC.WHITE + "    - " + potionEffect.getType().getName() + " (Amplifier: " + potionEffect.getAmplifier() + ") (Infinite: " + (potionEffect.getDuration() == Integer.MAX_VALUE ? "Yes" : "No") + ")"));
        player.sendMessage(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51));
    }
}