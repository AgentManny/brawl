package rip.thecraft.brawl.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  ChallengeType {

    DAILY(86400000),
    WEEKLY(604800000);

    private final long millis;

}
