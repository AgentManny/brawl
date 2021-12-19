package rip.thecraft.brawl.event;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.thecraft.brawl.challenges.rewards.RewardType;
import rip.thecraft.brawl.event.events.KingOfTheHill;
import rip.thecraft.brawl.event.events.KitFrenzy;
import rip.thecraft.brawl.event.king.KillTheKing;
import rip.thecraft.brawl.event.schematic.SchematicEvent;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum EventType {

    KING_OF_THE_HILL(
            KingOfTheHill.class,
            Material.DIAMOND_CHESTPLATE,
            "King Of The Hill",
            "KOTH",
            "A capture point is an area in the warzone, players must compete for control over them",
            ChatColor.DARK_PURPLE,
            ImmutableMap.of(
                    RewardType.CREDITS, 500,
                    RewardType.EXPERIENCE, 150
            )
    ),

    CAPTURE_POINT(
            KingOfTheHill.class,
            Material.BEACON,
            "Capture Point",
            "Capture Point",
            "Players must stand in the middle to gain points, multiple players are able to contest. " +
                    "You gain a point every second, first person to reach 150 point wins!",
            ChatColor.AQUA,
            ImmutableMap.of(
                    RewardType.CREDITS, 500,
                    RewardType.EXPERIENCE, 150
            )
    ),

    KILL_THE_KING(
            KillTheKing.class,
            Material.GOLD_HELMET,
            "Kill The King",
            "KING",
            "Players must eliminate the king. Kings can do increased damage regardless of their kit. " +
                    "Killing the king will reward you the king's rewards.",
            ChatColor.GOLD,
            ImmutableMap.of(
                    RewardType.CREDITS, 1500,
                    RewardType.EXPERIENCE, 750
            )
    ),

    SCHEMATIC(
            SchematicEvent.class,
            Material.EMPTY_MAP,
            "Schematic", "Schematic",
            "Spawns a schematic for a certain amount of time",
            ChatColor.BLUE,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    SPLATOON(
            null,
            Material.DIAMOND_BARDING,
            "Splatoon", "Splatoon",
            "Gain territory around the map, killing players spreads your territory.",
            ChatColor.YELLOW,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    BLOODLUST(
            null,
            Material.REDSTONE,
            "Bloodlust", "Bloodlust",
            "Killing players will give you double experience and increased credits to the highest killer.",
            ChatColor.RED,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    KIT_FRENZY(
            KitFrenzy.class,
            Material.EMERALD,
            "Kit Frenzy", "Frenzy",
            "Players leaving spawn will spawn with a completely random kit.",
            ChatColor.GREEN,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    ARCADE(
            null,
            Material.FIREWORK,
            "Arcade", "Arcade",
            "Kits will continiously rotate to a new kit while players fight.",
            ChatColor.YELLOW,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    FREE_KITS(
            null,
            Material.GOLD_INGOT,
            "Free Kits", "Free Kits",
            "Players have access to all kits.",
            ChatColor.GOLD,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    WARPER(
            null,
            Material.EYE_OF_ENDER,
            "Warper", "Warper",
            "Leaving spawn will teleport you to a new location.",
            ChatColor.DARK_PURPLE,
            ImmutableMap.of(
                    RewardType.CREDITS, 5,
                    RewardType.EXPERIENCE, 5
            )
    ),

    ;
    private Class<? extends Event> registry;

    private Material icon;

    private String displayName;
    private String shortName;

    private String description;

    private ChatColor color;

    private Map<RewardType, Integer> rewards;

    public String getPrefix() {
        return color.toString() + ChatColor.BOLD + shortName + " " + ChatColor.GRAY;
    }

    public static EventType getByName(String source) {
        String parsedSource = source.toUpperCase()
                .replace(" ", "")
                .replace("_", "");
        for (EventType type : values()) {
            if (type.name().replace("_", "").equalsIgnoreCase(parsedSource)) {
                return type;
            }
            if (type.getShortName().equalsIgnoreCase(parsedSource) || type.getDisplayName().equalsIgnoreCase(parsedSource)) {
                return type;
            }
        }
        return null;
    }

}
