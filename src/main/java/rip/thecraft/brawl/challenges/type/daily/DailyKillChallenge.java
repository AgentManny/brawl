package rip.thecraft.brawl.challenges.type.daily;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.server.util.chatcolor.CC;

public class DailyKillChallenge extends Challenge {

    public DailyKillChallenge() {
        super("Daily_Kill", "Kill 5 enemies.", ChallengeType.DAILY, 5);
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
