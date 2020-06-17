package rip.thecraft.brawl.player.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum StatisticType {

    KILLS("Kills", ChatColor.GREEN, Material.DIAMOND_SWORD),
    DEATHS("Deaths", ChatColor.RED, Material.SKULL_ITEM),

    KILLSTREAK("Killstreak", ChatColor.AQUA, Material.GOLD_CHESTPLATE),
    HIGHEST_KILLSTREAK("Highest Killstreak", ChatColor.DARK_AQUA, Material.DIAMOND_CHESTPLATE),

    CREDITS("Credits", ChatColor.GOLD, Material.GOLD_INGOT),

    KDR("KDR", ChatColor.DARK_RED, Material.REDSTONE_BLOCK),

    LEVEL("Level", ChatColor.LIGHT_PURPLE, Material.EXP_BOTTLE),

    EVENT_WINS("Event Wins", ChatColor.GREEN, Material.GOLD_BLOCK),

    DUEL_WINS("Duel Wins", ChatColor.AQUA, Material.DIAMOND_BLOCK),
    DUEL_LOSSES("Duel Losses", ChatColor.RED, Material.REDSTONE_BLOCK);

    private final String name;

    private final ChatColor color;
    private final Material icon;

    public static StatisticType parse(String input) {
        return Stream.of(values()).filter(s ->
                s.name().equalsIgnoreCase(input)
                        || s.name.replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))
        ).map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);
    }
}
