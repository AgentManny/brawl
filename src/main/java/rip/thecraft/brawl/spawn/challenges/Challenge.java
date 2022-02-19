package rip.thecraft.brawl.spawn.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.challenges.rewards.RewardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Todo make challenges configurable as the system we have right now allows that
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Challenge {

//    POGGERS(
//            ChallengeType.ABILITY,
//            Duration.TEST,
//            "Poggers",
//            "Poggers",
//            1,
//            ImmutableMap.of(RewardType.CREDITS, 69, RewardType.EXPERIENCE, 69)
//    ),
//
//    HUNTER(
//            ChallengeType.KILLS,
//            Duration.DAILY,
//            "Hunter",
//            "Kill 25 Players",
//            25,
//            ImmutableMap.of(RewardType.CREDITS, 500, RewardType.EXPERIENCE, 500)
//    ),
//
//    HUNTER_2(
//            ChallengeType.KILLS,
//            Duration.DAILY,
//            "Hunter 2",
//            "Kill 50 Players",
//            50,
//            ImmutableMap.of(RewardType.CREDITS, 500, RewardType.EXPERIENCE, 500)
//    ),
//
//    ENTREPRENEUR(
//            ChallengeType.CREDITS,
//            Duration.DAILY,
//            "Entrepreneur",
//            "Earn 5000 credits",
//            5000,
//            ImmutableMap.of(RewardType.CREDITS, 5000, RewardType.EXPERIENCE, 1000)
//    );

    private ChallengeType type;
    private Duration duration;

    private String name;
    private String description;

    private int maxValue;

    private Map<RewardType, Integer> rewards;

    public boolean isComplete(int value) {
        return value >= maxValue;
    }

    @AllArgsConstructor
    public enum Duration {

        TEST(TimeUnit.SECONDS.toMillis(15)),
        DAILY(TimeUnit.HOURS.toMillis(12)),
        WEEKLY(TimeUnit.DAYS.toMillis(7));

        public final long millis;

        public String getDisplayName() {
            return WordUtils.capitalize(name().toLowerCase());
        }

    }
}
