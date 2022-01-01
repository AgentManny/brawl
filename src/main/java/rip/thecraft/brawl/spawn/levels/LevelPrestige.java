package rip.thecraft.brawl.spawn.levels;

import lombok.Getter;
import org.bukkit.ChatColor;
import rip.thecraft.server.util.chatcolor.CC;

@Getter
public enum LevelPrestige {

    NONE(0, CC.GRAY),

    ONE(1, CC.WHITE),
    TWO(2, CC.LIGHT_PURPLE),

    THREE(3, CC.AQUA),
    FOUR(4, CC.YELLOW),
    FIVE(5, CC.GOLD),
    SEVEN(7,CC.DARK_PURPLE),
    NINE(9, CC.DARK_PURPLE + CC.BOLD),
    MAX(10, CC.DARK_RED + CC.BOLD);

    private int prestige;

    private String color;

    private static LevelPrestige[] values = values();

    LevelPrestige(int prestige, String color) {
        this.prestige = prestige;
        this.color = ChatColor.translateAlternateColorCodes('&', color);
    }


    public LevelPrestige next() {
        return values[(this.ordinal() + 1) % values.length];
    }

    public LevelPrestige previous() {
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }

    public static LevelPrestige getByPrestige(int prestige) {
        if (prestige >= 10) {
            return LevelPrestige.MAX;
        }
        for (LevelPrestige value : values) {
            LevelPrestige next = value.next();
            if (prestige >= value.prestige && prestige < next.prestige) {
                return value;
            }
        }
        return values[values.length - 1];
    }
}