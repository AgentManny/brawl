package rip.thecraft.brawl.challenges.type.weekly;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.server.util.chatcolor.CC;

public class WeeklyKillChallenge extends Challenge {

    public WeeklyKillChallenge() {
        super("Weekly_Kill", "Kill 100 enemies.", ChallengeType.WEEKLY, 100);
    }

    @Override
    public void increment(Player player) {
        this.currentProgress += 1;
        player.sendMessage(CC.YELLOW + "You have earned " + CC.WHITE + "1" + CC.YELLOW + " kill for the " + CC.GOLD + getDisplayName() + CC.YELLOW + " challenge.");
        this.complete(player);
    }

    @Override
    public void complete(Player player) {
        if (currentProgress >= maxProgress) {
            player.sendMessage(CC.YELLOW + "You have completed the " + CC.GOLD + getDisplayName() + CC.YELLOW + " challenge.");
        }
    }
}
