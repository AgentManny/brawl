package rip.thecraft.brawl.spawn.challenges.rewards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;

@Getter
@AllArgsConstructor
public enum RewardType {

    /** Completing a challenge will grant you credits */
    CREDITS(ChatColor.GOLD),

    /** Completing a challenge will grant you experience */
    EXPERIENCE(ChatColor.LIGHT_PURPLE);

    private ChatColor color;

    public String getName() {
        return WordUtils.capitalizeFully(name().toLowerCase());
    }

    public String getDisplayName() {
        return color + getName();
    }

    public void addRewards(Player player, int value) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        PlayerStatistic statistic = playerData.getStatistic();
        if (this == CREDITS) {
            statistic.add(StatisticType.CREDITS, value);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "+" + value + " credits");
        } else if (this == EXPERIENCE) {
            playerData.getLevel().addExp(player, value, (String) null);
        }
    }

}
