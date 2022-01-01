package rip.thecraft.brawl.player.achievements.type;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.player.achievements.Achievement;
import rip.thecraft.brawl.player.achievements.AchievementType;
import rip.thecraft.brawl.spawn.challenges.rewards.RewardType;
import rip.thecraft.brawl.player.PlayerData;

public class FirstTimeJoin extends Achievement {

    public FirstTimeJoin() {
        super("Joined for the first time", AchievementType.LOGIN);

        rewards = ImmutableMap.of(RewardType.CREDITS, 100);
    }

    public boolean isComplete(Player player, PlayerData data) {
        return !data.hasAchievement(this);
    }

}
