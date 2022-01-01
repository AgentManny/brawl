package rip.thecraft.brawl.spawn.challenges.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.challenges.Challenge;

@Setter
@Getter
@RequiredArgsConstructor
public class PlayerChallenge {

    private final Challenge challenge;

    private int value; // data tracker for player
    private boolean completed = true;

    public PlayerChallenge(Document document) {
        this.challenge = Challenge.valueOf(document.getString("challenge"));
        this.completed = document.getBoolean("completed");
        this.value = document.getInteger("value");
    }

    public Document getAsDocument() {
        return new Document("challenge", challenge.name())
                .append("value", value)
                .append("completed", completed);
    }

    public void increment(Player player, int value) {
        this.value += value;
        if (challenge.isComplete(value) && completed) {
            completed = false;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Completed " + ChatColor.BOLD + challenge.getDuration().getDisplayName() + ChatColor.LIGHT_PURPLE + " challenge: " + ChatColor.YELLOW + challenge.name() + ChatColor.LIGHT_PURPLE + "(" + ChatColor.YELLOW + value + "/" + challenge.getMaxValue() + ChatColor.LIGHT_PURPLE + "):");
            challenge.getRewards().forEach((rewardType, rewards) -> rewardType.addRewards(player, rewards));
        }
    }

    public float getProgress() {
        return Math.round((float) (value/ challenge.getMaxValue()) * 100);
    }

    public boolean isActive() {
        return !completed;
    }
}
