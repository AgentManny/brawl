package rip.thecraft.brawl.challenges;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.thecraft.brawl.challenges.rewards.RewardType;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum Challenge {

    HUNTER(
            ChallengeType.KILLS,
            ChallengeDuration.DAILY,
            "Hunter",
            "Kill 25 Players",
            25,
            ImmutableMap.of(RewardType.CREDITS, 500, RewardType.EXPERIENCE, 500)
    ),

    ENTREPRENEUR(
            ChallengeType.CREDITS,
            ChallengeDuration.WEEKLY,
            "Entrepreneur",
            "Earn 5000 credits",
            5000,
            ImmutableMap.of(RewardType.CREDITS, 5000, RewardType.EXPERIENCE, 1000)
    );

    private ChallengeType type;
    private ChallengeDuration duration;

    private String name;
    private String description;

    private int maxValue;

    private Map<RewardType, Integer> rewards;

    public boolean isComplete(int value) {
        return value >= maxValue;
    }

}
