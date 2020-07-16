package rip.thecraft.brawl.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class PlayerChallenge {

    protected final Challenge challenge;

    @Setter protected long timestamp; // time when activated;

    @Setter protected int progress; // data tracker for player

    @Setter private boolean completed = false; // make sure they don't complete the challenge twice
    @Setter private boolean active = false; // checks if they activated it

    public PlayerChallenge(Document document) {
        this.challenge = Challenge.valueOf(document.getString("challenge"));
        this.timestamp = document.getLong("timestamp");
        this.progress = document.getInteger("progress");
    }

    public Document getAsDocument() {
        return new Document("challenge", challenge.name())
                .append("progress", progress)
                .append("timestamp", timestamp);
    }

    public void increment(Player player, int value) {
        progress += value;
        if (challenge.isComplete(value) && !completed) {
            completed = true;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Completed " + ChatColor.BOLD + challenge.getDuration().getDisplayName() + ChatColor.LIGHT_PURPLE + " challenge: " + ChatColor.YELLOW + challenge.name() + ChatColor.LIGHT_PURPLE + "(" + ChatColor.YELLOW + value + "/" + challenge.getMaxValue() + ChatColor.LIGHT_PURPLE + "):");
            challenge.getRewards().forEach((rewardType, rewards) -> rewardType.addRewards(player, rewards));
        }
    }
}
