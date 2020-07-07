package rip.thecraft.brawl.kit.command;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
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
import rip.thecraft.brawl.kit.menu.KitListMenu;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KitCommand {

    private final Brawl brawl;

    @Command(names = { "kit", "k" })
    public void apply(Player player, Kit kit) {
        PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(player);
        if (!playerData.isSpawnProtection()) {
            player.sendMessage(ChatColor.RED + "You must have spawn protection to select your kit.");
            return;
        }

        if (playerData.hasKit(kit)) {
            kit.apply(player, true, true);
        } else {
            player.sendMessage(CC.RED + "You don't have permission to use this kit.");
        }
    }

    @Command(names = { "kit list", "kit list"}, permission = "op")
    public void list(Player player) {
        new KitListMenu().openMenu(player);
    }

    @Command(names = { "kit edit", "k edit" }, permission = "op")
    public void edit(Player player, Kit kit) {
        player.sendMessage(CC.GREEN + "Updating kit " + kit.getName() + "...");
        new KitEditMenu(kit).openMenu(player);
    }

    @Command(names = { "kit editor", "k editor" }, permission = "op")
    public void editor(Player player) {
        new KitEditorMenu().openMenu(player);
    }

    @Command(names = { "kit weight", "k weight" }, permission = "op")
    public void weight(Player player, Kit kit, int weight) {
        kit.setWeight(weight);
        brawl.getKitHandler().getKits().sort(Kit::compareTo);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " weight set to " + weight);
    }


    @Command(names = { "kit price", "k price" }, permission = "op")
    public void price(Player player, Kit kit, int price) {
        kit.setPrice(price);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " price set to " + price);
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
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()));
        kit.setItems(new Items(inventory.getContents()));
        brawl.getKitHandler().registerKit(kit);

        player.sendMessage(ChatColor.GREEN + "Created kit " + name + ".");
    }

    @Command(names =  { "kit remove", "k remove" }, permission = "op")
    public void remove(Player player, Kit kit) {
        brawl.getKitHandler().unregisterKit(kit);
        player.sendMessage(ChatColor.RED + "Removed kit " + kit.getName() + ".");
    }

    @Command(names =  { "kit setdescription", "k setdescription" }, permission = "op")
    public void description(Player player, Kit kit, String description) {
        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + " from \"" + kit.getDescription() + "\" to \"" + description.trim() + "\".");
        kit.setDescription(description.trim());
    }

    @Command(names =  { "kit ability", "k ability" }, permission = "op")
    public void ability(Player player, Kit kit, Ability ability) {
        if (kit.getAbilities().contains(ability)) {
            kit.getAbilities().remove(ability);
        } else {
            kit.getAbilities().add(ability);
        }
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + (kit.getAbilities().contains(ability) ? "now has" : "no longer has") + " ability " + ChatColor.BOLD + ability.getName() + ChatColor.GREEN + ".");
    }

    @Command(names =  { "kit setrank", "k setrank" }, permission = "op")
    public void rank(Player player, Kit kit, String rank) {
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

    @Command(names =  { "kit get", "k get" }, permission = "op")
    public void get(Player player, Kit kit) {
        kit.apply(player, false, false);
        new FancyMessage(ChatColor.GREEN + "You've been given " + kit.getName() + ChatColor.GREEN + ". Click Here to update this kit.")
                .tooltip(CC.GRAY + "Click to update this kit")
                .command("/kit update " + kit.getName())
                .send(player);
    }

    @Command(names =  { "kit icon", "k icon" }, permission = "op")
    public void icon(Player player, Kit kit) {
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + " icon is now the item you're holding.");
        kit.setIcon(player.getItemInHand() == null ? new ItemStack(Material.AIR) : player.getItemInHand());
    }

    @Command(names =  { "kit save", "k save" }, permission = "op")
    public void save(Player player) {
        brawl.getKitHandler().save();
        player.sendMessage(CC.GREEN + "Saved all kits");
    }

    @Command(names =  { "kit update", "k update" }, permission = "op")
    public void update(Player player, Kit kit) {
        PlayerInventory inventory = player.getInventory();
        kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
        kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()));
        kit.setItems(new Items(inventory.getContents()));
        new FancyMessage(ChatColor.GREEN + "Updated kit. Click Here to return back to spawn.")
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

        player.sendMessage(CC.LIGHT_PURPLE + "  Content: " + CC.YELLOW + kit.getItems().info());
        player.sendMessage(CC.LIGHT_PURPLE + "  Rank: " + CC.YELLOW + kit.getRankType().getDisplayName() + CC.WHITE + "[" + kit.getRankType().name() + "] ");
        player.sendMessage(CC.LIGHT_PURPLE + "  Abilities: " + CC.YELLOW + kit.getAbilities().stream().map(Ability::getName).collect(Collectors.joining(", ")));
        player.sendMessage(CC.LIGHT_PURPLE + "  Potion Effects:");
        kit.getPotionEffects().forEach(potionEffect -> player.sendMessage(CC.WHITE + "    - " + potionEffect.getType().getName() + " (Amplifier: " + potionEffect.getAmplifier() + ") (Infinite: " + (potionEffect.getDuration() == Integer.MAX_VALUE ? "Yes" : "No") + ")"));
        player.sendMessage(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51));
    }

}
