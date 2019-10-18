package gg.manny.brawl.leaderboard.menu.button;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class StatisticButton extends Button {

    private final StatisticType type;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> data = new ArrayList<>();
        Map<UUID, Double> values = Brawl.getInstance().getLeaderboard().getSpawnLeaderboards().get(type);

        data.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));

        int rank = 0;
        for (Map.Entry<UUID, Double> entry : values.entrySet()) {
            rank++;

            String base = (rank == 1 ? CC.GOLD : (rank <= 3 ? CC.YELLOW : CC.WHITE)) + rank + " ";
            double value = entry.getValue();
            double roundedValue = type == StatisticType.KDR ? Math.round(value * 10.) / 10. : Math.round(value);
            data.add(base + CC.WHITE + Pivot.getInstance().getProfileHandler().getProfile(entry.getKey()).getDisplayName() + CC.GRAY + " \u2758 " + CC.WHITE + roundedValue);
        }

        data.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));
        return new ItemBuilder(type.getIcon())
                .name(type.getColor() + type.getName() + ChatColor.GRAY + " \u2758 " + ChatColor.WHITE + "Top 10")
                .lore(data)
                .create();
    }

}
