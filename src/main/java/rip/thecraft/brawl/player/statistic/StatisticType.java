package rip.thecraft.brawl.player.statistic;

import gg.manny.streamline.util.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.thecraft.brawl.visual.tasks.LeaderboardUpdateTask;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum StatisticType {

    KILLS("Kills", ChatColor.GREEN, Material.DIAMOND_SWORD),
    DEATHS("Deaths", ChatColor.RED, Material.SKULL_ITEM),

    KDR("KDR", ChatColor.DARK_RED, Material.SULPHUR),

    KILLSTREAK("Killstreak", ChatColor.AQUA, Material.GOLD_CHESTPLATE, 0, true),
    HIGHEST_KILLSTREAK("Highest Killstreak", ChatColor.BLUE, Material.DIAMOND_CHESTPLATE),

    TOTAL_EXPERIENCE("Total EXP", ChatColor.DARK_AQUA, Material.EXP_BOTTLE),
    LEVEL("Level", ChatColor.LIGHT_PURPLE, Material.DIAMOND),
    PRESTIGE("Prestige", ChatColor.DARK_PURPLE, Material.NETHER_STAR, 1, false),

    CREDITS("Credits", ChatColor.GOLD, Material.GOLD_INGOT),

    EVENT_WINS("Events Won", ChatColor.GREEN, Material.DOUBLE_PLANT),

    DUEL_WINS("Duel Wins", ChatColor.AQUA, Material.IRON_CHESTPLATE),
    DUEL_LOSSES("Duel Losses", ChatColor.RED, Material.REDSTONE, 0, true),
    DUEL_WIN_STREAK("Duel Winstreak", ChatColor.GREEN, Material.EMERALD);

    private final String name;

    private final ChatColor color;
    private final Material icon;

    private double minValue = 0;

    private boolean hidden = false;

    public double getDefaultValue() {
        return this == LEVEL ? 1 : 0;
    }

    public String getFormatValue(double value) {
        switch (this) {
            case PRESTIGE: {
                return NumberUtils.romanNumerals((int)value);
            }
            case KDR: {
                return String.valueOf(Math.round(value * 10.) / 10.);
            }
        }
        return LeaderboardUpdateTask.STAT_FORMAT.format(value);
    }

    public StatisticType next() {
        StatisticType statistic = StatisticType.KILLS; // Fallback
        try {
            statistic = values()[(this.ordinal() + 1) % values().length];
        } catch (EnumConstantNotPresentException ignored) {
        }
        return statistic;
    }

    public static StatisticType parse(String input) {
        return Stream.of(values()).filter(s ->
                s.name().equalsIgnoreCase(input)
                        || s.name.replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))
        ).map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);
    }
}
