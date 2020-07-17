package rip.thecraft.brawl.challenges;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Setter
@Getter
public class PlayerChallenge {

    private final Challenge challenge;
    private final long expiresAt;

    private int value; // data tracker for player

    private boolean completed = false; // make sure they don't complete the challenge twice
    private boolean active = false; // checks if they activated it

    public PlayerChallenge(Challenge challenge) {
        Challenge.Duration duration = challenge.getDuration();
        this.challenge = challenge;
        this.expiresAt = System.currentTimeMillis() + duration.millis;
        if (duration == Challenge.Duration.DAILY) { // todo make it end at 12PM and not 12 hours after being activated
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
            LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);
        }
    }

    public PlayerChallenge(Document document) {
        this.challenge = Challenge.valueOf(document.getString("challenge"));
        this.expiresAt = document.getLong("expiresAt");
        this.value = document.getInteger("value");
        this.completed = document.getBoolean("complete");
    }

    public Document getAsDocument() {
        return new Document("challenge", challenge.name())
                .append("value", value)
                .append("complete", completed)
                .append("expiresAt", expiresAt);
    }

    public void increment(Player player, int value) {
        this.value += value;
        if (challenge.isComplete(value) && !completed) {
            completed = true;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Completed " + ChatColor.BOLD + challenge.getDuration().getDisplayName() + ChatColor.LIGHT_PURPLE + " challenge: " + ChatColor.YELLOW + challenge.name() + ChatColor.LIGHT_PURPLE + "(" + ChatColor.YELLOW + value + "/" + challenge.getMaxValue() + ChatColor.LIGHT_PURPLE + "):");
            challenge.getRewards().forEach((rewardType, rewards) -> rewardType.addRewards(player, rewards));
        }
    }

    public float getProgress() {
        return Math.round((float) (value/ challenge.getMaxValue()) * 100);
    }

    public boolean isActive() {
        return System.currentTimeMillis() > expiresAt;
    }

    public long getTimeLeft() {
        return System.currentTimeMillis() - expiresAt;
    }

    public boolean isCompleted() {
        return completed;
    }

}
