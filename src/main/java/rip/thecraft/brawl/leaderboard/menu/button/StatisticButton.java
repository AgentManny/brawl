package rip.thecraft.brawl.leaderboard.menu.button;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.spartan.menu.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class StatisticButton extends Button {

    private final StatisticType type;

    @Override
    public String getName(Player player) {
        return type.getColor() + type.getName() + ChatColor.GRAY + " \u2758 " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public Material getMaterial(Player player) {
        return type.getIcon();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = new ArrayList<>();
        Map<String, Double> values = Brawl.getInstance().getLeaderboard().getSpawnLeaderboards().get(type);

        int entries = 0;
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            String prefix = ChatColor.WHITE.toString();
            switch (++entries) {
                case 1: {
                    prefix = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD;
                    break;
                }
                case 2: {
                    prefix = ChatColor.LIGHT_PURPLE.toString();
                    break;
                }
                case 3: {
                    prefix = ChatColor.YELLOW.toString();
                    break;
                }
            }
            double value = entry.getValue();
            double roundedValue = type == StatisticType.KDR ? Math.round(value * 10.) / 10. : Math.round(value);
            lines.add(prefix + ChatColor.WHITE + entry.getKey() + ChatColor.GRAY + " \u2758 " + ChatColor.WHITE + roundedValue);
        }

        lines.add(0, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));
        lines.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));
        return lines;
    }

}
