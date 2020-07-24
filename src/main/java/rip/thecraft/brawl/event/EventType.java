package rip.thecraft.brawl.event;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.thecraft.brawl.challenges.rewards.RewardType;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum EventType {

    CAPTURE_POINT(
            Material.NETHER_STAR,
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
            Material.GOLD_HELMET,
            "Kill THe King",
            "KING",
            "Players must eliminate the king. Kings can do increased damage regardless of their kit. " +
                    "Killing the king will reward you the king's rewards.",
            ChatColor.GOLD,
            ImmutableMap.of(
                    RewardType.CREDITS, 1500,
                    RewardType.EXPERIENCE, 750
            )
    ),

    KING_OF_THE_HILL(
            Material.DIAMOND_CHESTPLATE,
            "King Of The Hill",
            "KOTH",
            "A capture point is an area in the warzone, players must compete for control over them",
            ChatColor.DARK_PURPLE,
            ImmutableMap.of(
                    RewardType.CREDITS, 500,
                    RewardType.EXPERIENCE, 150
            )
    );

    private Material icon;

    private String displayName;
    private String shortName;

    private String description;

    private ChatColor color;

    private Map<RewardType, Integer> rewards;

    public String getPrefix() {
        return color + "[" + shortName + "] " + ChatColor.WHITE;
    }

}
