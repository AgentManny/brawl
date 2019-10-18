package gg.manny.brawl.leaderboard.menu.button;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
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
public class EloButton extends Button {

    private final MatchLoadout type;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> data = new ArrayList<>();
        Map<UUID, Integer> values = Brawl.getInstance().getLeaderboard().getEloLeaderboards().get(type);

        data.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));

        int rank = 0;
        for (Map.Entry<UUID, Integer> entry : values.entrySet()) {
            rank++;

            String base = (rank == 1 ? CC.DARK_PURPLE + CC.BOLD : (rank < 3 ? CC.LIGHT_PURPLE : rank < 4 ? CC.YELLOW : CC.WHITE)) + rank + " ";
            data.add(base + CC.WHITE + Pivot.getInstance().getProfileHandler().getProfile(entry.getKey()).getDisplayName() + CC.GRAY + " \u2758 " + CC.WHITE + entry.getValue());
        }

        data.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 30));
        return new ItemBuilder(type.getIcon())
                .name(type.getColor() + type.getName() + ChatColor.GRAY + " \u2758 " + ChatColor.WHITE + "Top 10")
                .lore(data)
                .create();
    }

}
