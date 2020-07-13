package rip.thecraft.brawl.challenges;

import com.google.common.collect.Lists;
import lombok.Getter;
import rip.thecraft.brawl.challenges.type.daily.DailyKillChallenge;

import java.util.Arrays;
import java.util.List;

@Getter
public class ChallengeHandler {

    private List<Challenge> dailyChallenges, weeklyChallenges;

    public ChallengeHandler() {
        this.dailyChallenges = Arrays.asList(
                new DailyKillChallenge()
        );

        this.weeklyChallenges = Lists.newArrayList();
    }

    public Challenge getByName(String challengeName) {
        for (Challenge challenge : this.dailyChallenges) {
            if (challenge.getName().equalsIgnoreCase(challengeName)) {
                return challenge;
            }
        }

        for (Challenge challenge : this.weeklyChallenges) {
            if (challenge.getName().equalsIgnoreCase(challengeName)) {
                return challenge;
            }
        }

        return null;
    }

}
