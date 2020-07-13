package rip.thecraft.brawl.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
public enum  ChallengeType {

    DAILY(TimeUnit.DAYS.toMillis(1)),
    WEEKLY(TimeUnit.DAYS.toMillis(7));

    private final long millis;

}
