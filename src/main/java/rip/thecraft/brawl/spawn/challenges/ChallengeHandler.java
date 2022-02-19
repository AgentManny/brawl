package rip.thecraft.brawl.spawn.challenges;

import lombok.Getter;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.challenges.command.ChallengeCreateCommand;

import java.util.ArrayList;
import java.util.List;

public class ChallengeHandler {

    public static final int MAX_DAILY_CHALLENGES = 2;
    public static final int MAX_WEEKLY_CHALLENGES = 1;

    @Getter
    private List<Challenge> challenges = new ArrayList<>();

    public ChallengeHandler(Brawl plugin) {
        plugin.getCommandService()
                .register(new ChallengeCreateCommand(this), "Challenge");
    }

    public Challenge getChallengeByName(String name) {
        for (Challenge challenge : challenges) {
            if (challenge.getName().replace(" ", "").replace("_", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return challenge;
            }
        }
        return null;
    }

    public List<Challenge> getByDuration(Challenge.Duration duration) {
        List<Challenge> challenges = new ArrayList<>();
        for (Challenge value : challenges) {
            if (value.getDuration() == duration) {
                challenges.add(value);
            }
        }
        return challenges;
    }

    @Deprecated
    public Challenge getRandomChallenge() {
        return challenges.get(Brawl.RANDOM.nextInt(challenges.size()));
    }
}
