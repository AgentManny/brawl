package rip.thecraft.brawl.levels;

import lombok.Getter;
import org.bukkit.ChatColor;
import rip.thecraft.brawl.util.BukkitUtil;
import rip.thecraft.server.util.chatcolor.CC;

@Getter
public enum Levels {

    ONE(1, 20, CC.GRAY),

    THREE(5, 25, CC.GRAY),

    TEN(10, 30, CC.WHITE),

    TWENTY(20, 35, 1.15, CC.AQUA),

    THIRTY(30, 40, 1.2, CC.DARK_AQUA),

    FORTY(40, 45, 1.25, CC.BLUE),
    FIFTY(50, 50, 1.3, CC.YELLOW),
    SIXTY(60, 55, 1.35, CC.GOLD + CC.BOLD),
    SEVENTY(70, 75, 1.45,CC.RED + CC.BOLD),
    EIGHTY(80, 85, 1.5, CC.DARK_RED + CC.BOLD),
    NINETY(90, 95, 1.75, CC.LIGHT_PURPLE + CC.BOLD),
    HUNDRED(100, 100, 1.75, CC.DARK_PURPLE + CC.BOLD);

    private int level;

    private double xpMultiplier; // XP takes way too long to grind higher on, so we'll just give multipliers

    private int experience;
    private String color;

    private static Levels[] values = values();

    private LevelFeature[] features;

    Levels(int level, int experience, double xpMultiplier, String color, LevelFeature... features) {
        this.level = level;
        this.experience = experience;
        this.xpMultiplier = xpMultiplier;
        this.color = ChatColor.translateAlternateColorCodes('&', color);
        this.features = features;
    }

    Levels(int level, int experience, String color, LevelFeature... features) {
        this(level, experience, 1, color, features);
    }

    public Levels next() {
        return values[(this.ordinal() + 1) % values.length];
    }

    public Levels previous() {
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }

    public static Levels getByLevel(int level) {
        for (Levels value : values) {
            Levels next = value.next();
            if (level >= value.level && level < next.level) {
                return value;
            }
        }
        return values[values.length - 1];
    }

    public static String getPrefix(Level playerLevel) {
        Levels level = getByLevel(playerLevel.getCurrentLevel());
        return ChatColor.GRAY + "[" + (playerLevel.isPrestige() ? ChatColor.DARK_PURPLE + BukkitUtil.romanNumerals(playerLevel.getPrestige()) + ChatColor.GRAY + "-" : "") + level.color + playerLevel.getCurrentLevel() + ChatColor.GRAY
                + "]";
    }
}