package rip.thecraft.brawl.kit.command;

import com.google.common.base.Strings;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.menu.KitEditMenu;
import rip.thecraft.brawl.kit.editor.menu.KitEditorMenu;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.kit.unlock.UnlockMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.brawl.util.conversation.PromptBuilder;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

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

    @Command(names = { "kit unlock", "k unlock" })
    public static void unlock(Player player) {
        new UnlockMenu(player).open(player);
    }

    @Command(names = {"kit apply", "kit force", "k apply", "k force"}, permission = "falcon.command.kit.apply")
    public static void apply(Player player, Kit kit, Player target){
        kit.apply(target, false, true);
        player.sendMessage(ChatColor.YELLOW + "You have applied kit " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.YELLOW + " to " + target.getName()
                + ChatColor.YELLOW + ".");
    }

    @Command(names = {"kit clear", "kit forceclear", "k clear", "k forceclear"}, permission = "falcon.command.kit.clear")
    public static void clear(Player player, PlayerData target){
        target.setPreviousKit(target.getSelectedKit());
        target.setSelectedKit(null);
        PlayerUtil.resetInventory(target.getPlayer());
        player.sendMessage(ChatColor.YELLOW + "You have cleared " + ChatColor.DARK_PURPLE + target.getName() + "'s " + ChatColor.YELLOW + "kit.");
    }

    @Command(names = {"rkit", "raiduskit"}, permission = "falcon.command.kit.rkit")
    public static void rkit(Player player, Kit kit, int radius){
        if(radius > 100){
            player.sendMessage(ChatColor.RED + "Radius cannot be greater than 100");
            return;
        }

        for(Player around : PlayerUtil.getNearbyPlayers(player.getLocation(), radius)){
            PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(around);

            kit.apply(data.getPlayer(), true, true);
        }

        int playersGiven = PlayerUtil.getNearbyPlayers(player.getLocation(), radius).size();
        player.sendMessage(ChatColor.YELLOW + "You have applied kit " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.YELLOW + " to " +
                ChatColor.DARK_PURPLE + playersGiven + ChatColor.YELLOW + " players in a radius of " + ChatColor.DARK_PURPLE + radius + ChatColor.YELLOW + ".");
    }


    @Command(names = { "ek unbreaking", "unbreaking" }, permission = "op")
    public static void edit(Player player) {
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.spigot().setUnbreakable(true);
            itemStack.setItemMeta(itemMeta);
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + "Set item to unbreaking");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have an item in your hand!");
        }
    }


    @Command(names = {"kit create", "k create"}, permission = "op")
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

    @Command(names = {"kit remove", "k remove"}, permission = "op")
    public static void remove(Player player, Kit kit) {
        Brawl.getInstance().getKitHandler().unregisterKit(kit);
        player.sendMessage(ChatColor.RED + "Removed kit " + kit.getName() + ".");
    }

    @Command(names = {"editkit weight", "ek weight"}, permission = "op")
    public static void setWeight(Player player, Kit kit, int weight) {
        kit.setWeight(weight);
        Brawl.getInstance().getKitHandler().getKits().sort(Kit::compareTo);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " weight set to " + weight);
    }


    @Command(names = {"editkit setprice", "ek setprice"}, permission = "op")
    public static void setPrice(Player player, Kit kit, int price) {
        kit.setPrice(price);
        player.sendMessage(ChatColor.GREEN + kit.getName() + " price set to " + price);
    }

    @Command(names = {
            "editkit setdescription", "ek setdescription",
            "editkit setdesc", "ek setdesc",
            "editkit desc", "ek desc"
    }, permission = "op")
    public static void description(Player player, Kit kit, @Param(defaultValue = "$") String description) {
        if (description.equals("$")) {
            new PromptBuilder(player, ChatColor.GREEN + "Enter the kit description:")
                    .input((input) -> {
                        player.sendMessage(ChatColor.GREEN + "Description set to: " + ChatColor.YELLOW + input);
                        kit.setDescription(input);
                    }).start();
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + " from \"" + kit.getDescription() + "\" to \"" + description.trim() + "\".");
        kit.setDescription(description.trim());
    }

    @Command(names = {"editkit setrank", "ek setrank"}, permission = "op")
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

    @Command(names = {"editkit seticon", "ek seticon"}, permission = "op")
    public static void icon(Player player, Kit kit) {
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + " icon is now the item you're holding.");
        kit.setIcon(player.getItemInHand() == null ? new ItemStack(Material.AIR) : player.getItemInHand());
    }

    @Command(names = {"editkit ability", "ek ability"}, permission = "op")
    public static void ability(Player player, Kit kit, Ability ability) {
        if (kit.getAbilities().contains(ability)) {
            kit.getAbilities().remove(ability);
        } else {
            kit.getAbilities().add(ability);
        }
        player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + (kit.getAbilities().contains(ability) ? " now has" : " no longer has") + " ability " + ChatColor.BOLD + ability.getName() + ChatColor.GREEN + ".");
    }

    @Command(names = {"editkit load", "ek load"}, permission = "op")
    public static void load(Player player, Kit kit) {
        kit.apply(player, false, false);
        new FancyMessage(ChatColor.GREEN + "You've been given " + kit.getName() + ChatColor.GREEN + ". Click Here to update this kit.")
                .tooltip(CC.GRAY + "Click to update this kit")
                .command("/kit update " + kit.getName())
                .send(player);
    }

    @Command(names = {"editkit save", "ek save"}, permission = "op")
    public static void save(Player player, Kit kit) {
        kit.save();
        player.sendMessage(CC.GREEN + "Saved " + kit.getName() + " kit.");
    }

    @Command(names = {"editkit update", "ek update"}, permission = "op")
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

    @Command(names = {"editkit info", "ek info"}, permission = "op")
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