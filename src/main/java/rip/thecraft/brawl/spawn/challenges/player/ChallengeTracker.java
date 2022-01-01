package rip.thecraft.brawl.spawn.challenges.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.challenges.Challenge;
import rip.thecraft.brawl.player.PlayerData;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.next;

@RequiredArgsConstructor
public class ChallengeTracker {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm a", Locale.ENGLISH);

    public static final LocalTime RESET_TIME = LocalTime.NOON; // Challenges reset at 12PM (midday)
    public static final DayOfWeek WEEKLY_RESET_DAY = DayOfWeek.THURSDAY; // Weekly challenges reset on Thursday (at RESET_TIME)

    private final transient PlayerData playerData;

    private long dailyExpiry = -1, weeklyExpiry = -1;
    @Getter private final Multimap<Challenge.Duration, PlayerChallenge> challenges = ArrayListMultimap.create();

    /**
     * Refreshes daily and weekly challenges including the
     * expiry times.
     */
    public void refresh(boolean forced) {
        LocalDate now = LocalDate.now(ZoneId.systemDefault()); // todo make TIMEZONE configurable
        if (forced || dailyExpiry < System.currentTimeMillis()) {
            challenges.removeAll(Challenge.Duration.DAILY);

            LocalDateTime upcomingDay = LocalDateTime.of(now, RESET_TIME).plusDays(1);
            dailyExpiry = upcomingDay.toInstant(ZoneOffset.UTC).toEpochMilli();

            // Adds the daily challenges
            List<Challenge> challenges = Challenge.getByDuration(Challenge.Duration.DAILY);
            List<PlayerChallenge> addedChallenges = new ArrayList<>();
            while (!challenges.isEmpty() && challenges.size() > Challenge.MAX_DAILY_CHALLENGES) {
                Challenge challenge = challenges.get(Brawl.RANDOM.nextInt(challenges.size() - 1));

                addedChallenges.add(new PlayerChallenge(challenge));
                challenges.remove(challenge); // To prevent it from being added
            }
            this.challenges.putAll(Challenge.Duration.WEEKLY, addedChallenges);
        }

        if (forced || weeklyExpiry < System.currentTimeMillis()) {
            challenges.removeAll(Challenge.Duration.WEEKLY);

            LocalDate upcomingDay = now.with(next(WEEKLY_RESET_DAY));
            LocalDateTime weeklyReset = LocalDateTime.of(upcomingDay, RESET_TIME);
            dailyExpiry = weeklyReset.toInstant(ZoneOffset.UTC).toEpochMilli();

            List<Challenge> challenges = Challenge.getByDuration(Challenge.Duration.WEEKLY);
            List<PlayerChallenge> addedChallenges = new ArrayList<>();
            while (!challenges.isEmpty() && challenges.size() > Challenge.MAX_WEEKLY_CHALLENGES) {
                Challenge challenge = challenges.get(Brawl.RANDOM.nextInt(challenges.size() - 1));

                addedChallenges.add(new PlayerChallenge(challenge));
                challenges.remove(challenge); // To prevent it from being added
            }
            this.challenges.putAll(Challenge.Duration.WEEKLY, addedChallenges);
        }
    }

    /**
     * Loads challenge tracker from the player's data
     *
     * @param document Data to load
     */
    public void load(Document document) {
        if (document == null) {
            refresh(true);
            return;
        }

        dailyExpiry = document.getLong("daily-expiry");
        weeklyExpiry = document.getLong("weekly-expiry");
        List<Document> challengesDoc = (List<Document>) document.get("challenges");
        challengesDoc.forEach(doc -> {
            PlayerChallenge challenge = new PlayerChallenge(document);
            this.challenges.get(challenge.getChallenge().getDuration()).add(challenge);
        });

        refresh(false);
    }

    /**
     * Serializes challenge tracker into a Document
     * @return Document
     */
    public Document toDocument() {
        Document document = new Document("daily-expiry", dailyExpiry)
                .append("weekly-expiry", weeklyExpiry);

        List<Document> challenges = new ArrayList<>();
        this.challenges.entries().forEach((entry) -> challenges.add(entry.getValue().getAsDocument()));
        document.append("challenges", challenges);
        return document;
    }

    public long getDailyExpiry() {
        return System.currentTimeMillis() - dailyExpiry;
    }

    public long getWeeklyExpiry() {
        return System.currentTimeMillis() - weeklyExpiry;
    }
}
