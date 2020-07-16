package rip.thecraft.brawl.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;

import java.util.concurrent.TimeUnit;


@Getter
@AllArgsConstructor
public enum ChallengeDuration {

    DAILY(TimeUnit.HOURS.toMillis(12)),
    WEEKLY(TimeUnit.DAYS.toMillis(7));

    private final long millis;

    public String getDisplayName() {
        return WordUtils.capitalize(name().toLowerCase());
    }

}