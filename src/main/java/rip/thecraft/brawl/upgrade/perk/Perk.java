package rip.thecraft.brawl.upgrade.perk;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;

@Getter
public enum Perk {

    BLAZING_ARROWS(
            "Blazing Arrows",
            Material.BLAZE_POWDER,
            300,
            ChatColor.GRAY + "Shooting your bow has a " + ChatColor.GOLD + "30% chance",
            ChatColor.GRAY + "of it being set on fire."
    ),

    NOURISHMENT(
            "Nourishment",
            Material.BREAD,
            500,
            ChatColor.GRAY + "Every kill has a 25% chance to replenish",
            ChatColor.GRAY + "your hotbar."
    ),

//    GHOST(
//            "Ghost",
//            Material.DEAD_BUSH,
//            300,
//            ChatColor.GRAY + "Undetected to any " + ChatColor.WHITE + "long range abilities" + ChatColor.GRAY + "."
//    ),

    SCAVENGER(
            "Scavenger",
            Material.IRON_CHESTPLATE,
            500,
            ChatColor.GRAY + "Refill stations will always have",
            ChatColor.GRAY + "soup/potions in them."
    ),

    LIGHTWEIGHT(
            "Lightweight",
            Material.DIAMOND_BOOTS,
            750,
            ChatColor.GRAY + "Reduces fall damage by 50% if inflicted",
            ChatColor.GRAY + "by a player."
    ),

//    TRACKER(
//            "Tracker",
//            Material.COMPASS,
//            750,
//            ChatColor.GRAY + "Spawn with a tracker that shows location of",
//            ChatColor.GRAY + "enemy players nearby."
//    ),

    MEDIC(
            "Medic",
            Material.GOLDEN_APPLE,
            750,
            ChatColor.GRAY + "Enemy kills grants nearby teammates",
            ChatColor.LIGHT_PURPLE + "Regeneration II" + ChatColor.GRAY + " for 3 seconds."
    ),

    VENOM(
            "Venom",
            Material.SPIDER_EYE,
            750,
            ChatColor.GRAY + "Attacking players has a 5% chance of " + ChatColor.DARK_GREEN + "poisoning",
            ChatColor.GRAY + "them for 3 seconds."
    ),

    OVERCLOCK(
            "Overclock",
            Material.WATCH,
            1250,
            ChatColor.GRAY + "Reduces your ability cooldown by " + ChatColor.WHITE + "50%" + ChatColor.GRAY + "."
    ),

    KAMIKAZE(
            "Kamikaze",
            Material.SKULL_ITEM, 4,
            1750,
            ChatColor.GRAY + "Chance of summoning a " + ChatColor.GREEN + "Charged Creeper" + ChatColor.GRAY + " in your",
            ChatColor.GRAY + "death location."
    ),

    REVENGE(
            "Revenge",
            Material.IRON_SWORD,
            750,
            ChatColor.GRAY + "Triple your rewards for killing a",
            ChatColor.GRAY + "player that killed you."
    ),

    HARDLINE(
            "Hardline",
            Material.CHEST,
            1000,
            ChatColor.GRAY + "Killstreaks require " + ChatColor.YELLOW + "one less point" + ChatColor.GRAY + " to",
            ChatColor.GRAY + "obtain."
    ),

    ADRENALINE(
            "Adrenaline",
            Material.SUGAR,
            750,
            ChatColor.GRAY + "When below " + ChatColor.RED + "3.5 hearts" + ChatColor.GRAY + ", chance of",
            ChatColor.GRAY + "gaining Resistance and Speed for 3 seconds."
    ),

    DISTORTION(
            "Distortion",
            Material.COAL, 1,
            750,
            ChatColor.GRAY + "Attacking players has a 5% chance of",
            ChatColor.GRAY + "blinding your enemies."
    ),

    REAPER(
            "Reaper",
            Material.STONE_HOE,
            1000,
            ChatColor.GRAY + "Attacking players has a 5% chance of",
            ChatColor.GRAY + "giving the enemy " + ChatColor.DARK_GRAY + "Wither II" + ChatColor.GRAY + " for 2s",
            ChatColor.GRAY + "while also regenerating yourself."
    ),

    BULLDOZER(
            "Bulldozer",
            Material.ANVIL,
            2000,
            ChatColor.GRAY + "Killing a player grants you " + ChatColor.RED + "Strength I",
            ChatColor.GRAY + "for 5 seconds."
    ),

    JUGGERNAUT(
            "Juggernaut",
            Material.DIAMOND_CHESTPLATE,
            2000,
            ChatColor.GRAY + "Enemy kills give you " + ChatColor.LIGHT_PURPLE + "Regeneration I",
            ChatColor.GRAY + "up to 10 seconds." // Varies from 5 - 10 seconds
    );

    private String name;
    private Material icon;
    private byte iconData;
    private int credits;
    private String[] lore;

    Perk(String name, Material icon, int credits, String... lore) {
        this.name = name;
        this.icon = icon;
        this.iconData = 0;
        this.credits = credits;
        this.lore = lore;
    }

    Perk(String name, Material icon, int data, int credits, String... lore) {
        this.name = name;
        this.icon = icon;
        this.iconData = (byte) data;
        this.credits = credits;
        this.lore = lore;
    }

    public static boolean hasPerk(Player player, Perk perk) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        return !playerData.getUnlockedPerks().isEmpty() && playerData.usingPerk(perk);
    }

    public static Perk getPerk(String name) {
        for (Perk perk : Perk.values()) {
            if (perk.getName().equalsIgnoreCase(name)) {
                return perk;
            }
        }
        return null;
    }

    public boolean contains(Perk[] perks) {
        for (Perk perk : perks) {
            if (this == perk) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return ChatColor.stripColor(Strings.join(lore, " "));
    }
}
