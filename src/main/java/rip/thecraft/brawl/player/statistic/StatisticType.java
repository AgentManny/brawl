package rip.thecraft.brawl.player.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum StatisticType {

    KILLS("Kills", ChatColor.GREEN, Material.DIAMOND_SWORD),
    DEATHS("Deaths", ChatColor.RED, Material.SKULL_ITEM),

    KILLSTREAK("Killstreak", ChatColor.AQUA, Material.GOLD_CHESTPLATE),
    HIGHEST_KILLSTREAK("Highest Killstreak", ChatColor.DARK_AQUA, Material.DIAMOND_CHESTPLATE),
    LEVEL("Level", ChatColor.LIGHT_PURPLE, Material.EXP_BOTTLE),

    CREDITS("Credits", ChatColor.GOLD, Material.GOLD_INGOT),

    KDR("KDR", ChatColor.DARK_RED, Material.REDSTONE_BLOCK),

    EVENT_WINS("Event Wins", ChatColor.GREEN, Material.GOLD_BLOCK),

    DUEL_WINS("Duel Wins", ChatColor.AQUA, Material.DIAMOND_BLOCK, true),
    DUEL_LOSSES("Duel Losses", ChatColor.RED, Material.REDSTONE_BLOCK, true);

    private final String name;

    private final ChatColor color;
    private final Material icon;

    private boolean hidden = false;

    public static StatisticType parse(String input) {
        return Stream.of(values()).filter(s ->
                s.name().equalsIgnoreCase(input)
                        || s.name.replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))
        ).map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);
    }
}
