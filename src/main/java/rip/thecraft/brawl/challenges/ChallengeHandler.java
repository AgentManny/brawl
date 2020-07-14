package rip.thecraft.brawl.challenges;

import lombok.Getter;
import rip.thecraft.brawl.challenges.type.daily.DailyKillChallenge;
import rip.thecraft.brawl.challenges.type.weekly.WeeklyKillChallenge;

import java.util.Arrays;
import java.util.List;

@Getter
public class ChallengeHandler {

    private List<Challenge> dailyChallenges, weeklyChallenges;

    public ChallengeHandler() {
        // Only include the 3 challenges for the day / week
        // The menu loops through the entire list.
        this.dailyChallenges = Arrays.asList(
                new DailyKillChallenge()
        );

        this.weeklyChallenges = Arrays.asList(
                new WeeklyKillChallenge()
        );

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
