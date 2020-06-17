package rip.thecraft.brawl.util;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.spartan.util.Lang;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Strings.repeat;

public class BukkitUtil {

    private static final ImmutableMap<PotionEffectType, ChatColor> POTION_EFFECT_CHAT_COLOR_MAP;
    private static final ImmutableMap<DyeColor, ChatColor> DYE_CHAT_COLOUR_MAP;
    private static final LinkedHashMap<String, Integer> ROMAN_NUMERALS = new LinkedHashMap<>();

    static {

        ROMAN_NUMERALS.put("M", 1000);
        ROMAN_NUMERALS.put("CM", 900);
        ROMAN_NUMERALS.put("D", 500);
        ROMAN_NUMERALS.put("CD", 400);
        ROMAN_NUMERALS.put("C", 100);
        ROMAN_NUMERALS.put("XC", 90);
        ROMAN_NUMERALS.put("L", 50);
        ROMAN_NUMERALS.put("XL", 40);
        ROMAN_NUMERALS.put("X", 10);
        ROMAN_NUMERALS.put("IX", 9);
        ROMAN_NUMERALS.put("V", 5);
        ROMAN_NUMERALS.put("IV", 4);
        ROMAN_NUMERALS.put("I", 1);

        DYE_CHAT_COLOUR_MAP = (ImmutableMap.<DyeColor, ChatColor>builder().
                put(DyeColor.LIGHT_BLUE, ChatColor.AQUA).
                put(DyeColor.BLACK, ChatColor.BLACK).
                put(DyeColor.CYAN, ChatColor.DARK_AQUA).
                put(DyeColor.BLUE, ChatColor.BLUE).
                put(DyeColor.GRAY, ChatColor.DARK_GRAY).
                put(DyeColor.GREEN, ChatColor.DARK_GREEN).
                put(DyeColor.PURPLE, ChatColor.DARK_PURPLE).
                put(DyeColor.ORANGE, ChatColor.GOLD).
                put(DyeColor.SILVER, ChatColor.GRAY).
                put(DyeColor.LIME, ChatColor.GREEN).
                put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE).
                put(DyeColor.PINK, ChatColor.LIGHT_PURPLE).
                put(DyeColor.RED, ChatColor.RED).
                put(DyeColor.WHITE, ChatColor.WHITE).
                put(DyeColor.YELLOW, ChatColor.YELLOW).build());

        POTION_EFFECT_CHAT_COLOR_MAP = (ImmutableMap.<PotionEffectType, ChatColor>builder()

                .put(PotionEffectType.ABSORPTION, ChatColor.GOLD)
                .put(PotionEffectType.BLINDNESS, ChatColor.BLACK)
                .put(PotionEffectType.CONFUSION, ChatColor.DARK_GREEN)
                .put(PotionEffectType.DAMAGE_RESISTANCE, ChatColor.BLUE)
                .put(PotionEffectType.FAST_DIGGING, ChatColor.YELLOW)
                .put(PotionEffectType.FIRE_RESISTANCE, ChatColor.GOLD)
                .put(PotionEffectType.HARM, ChatColor.DARK_PURPLE)
                .put(PotionEffectType.HEAL, ChatColor.RED)
                .put(PotionEffectType.HEALTH_BOOST, ChatColor.RED)
                .put(PotionEffectType.HUNGER, ChatColor.DARK_GREEN)
                .put(PotionEffectType.INCREASE_DAMAGE, ChatColor.RED)
                .put(PotionEffectType.INVISIBILITY, ChatColor.GRAY)
                .put(PotionEffectType.JUMP, ChatColor.WHITE)
                .put(PotionEffectType.NIGHT_VISION, ChatColor.GREEN)
                .put(PotionEffectType.POISON, ChatColor.DARK_GREEN)
                .put(PotionEffectType.REGENERATION, ChatColor.LIGHT_PURPLE)
                .put(PotionEffectType.SATURATION, ChatColor.GREEN)
                .put(PotionEffectType.WITHER, ChatColor.DARK_GRAY)
                .put(PotionEffectType.SLOW, ChatColor.DARK_GRAY)

                .put(PotionEffectType.WEAKNESS, ChatColor.GRAY)
                .put(PotionEffectType.WATER_BREATHING, ChatColor.BLUE)
                .put(PotionEffectType.SPEED, ChatColor.AQUA)
                .put(PotionEffectType.SLOW_DIGGING, ChatColor.GRAY)
                .build());
    }

    public static ChatColor toChatColor(PotionEffectType type) {
        return POTION_EFFECT_CHAT_COLOR_MAP.getOrDefault(type, ChatColor.DARK_PURPLE);
    }

    public static ChatColor toChatColor(DyeColor color) {
        return DYE_CHAT_COLOUR_MAP.getOrDefault(color, ChatColor.WHITE);
    }

    public static String getFriendlyName(PotionEffect effect) {
        return toChatColor(effect.getType()) + Lang.fromPotionEffectType(effect.getType()) + ' ' + romanNumerals(effect.getAmplifier() + 1);
    }

    public static String getFriendlyName(DyeColor color) {
        return toChatColor(color) + WordUtils.capitalizeFully(color.name().toLowerCase().replace("_", " ")
                .replace("silver", "light gray")
        );
    }

    public static String romanNumerals(int value) {
        StringBuilder res = new StringBuilder();
        for (final Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
            final int matches = value / entry.getValue();
            res.append(repeat(entry.getKey(), matches));
            value %= entry.getValue();
        }
        return res.toString();
    }
}