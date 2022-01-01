package rip.thecraft.brawl.player.achievements;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.challenges.rewards.RewardType;
import rip.thecraft.brawl.player.PlayerData;

import java.util.Map;

@RequiredArgsConstructor
public abstract class Achievement {

    private final String name;
    private final AchievementType type;

    protected Map<RewardType, Integer> rewards;

    public String getId() {
        return name.toLowerCase().replace(" ", "-");
    }

    public abstract boolean isComplete(Player player, PlayerData playerData);

}
