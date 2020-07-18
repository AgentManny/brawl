package rip.thecraft.brawl.challenges.command;

import com.google.common.collect.Multimap;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.player.ChallengeTracker;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.util.TimeUtils;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ChallengeCommand {

    @Command(names = "challenge refresh", permission = "op", async = true)
    public static void refreshChallenges(Player player, PlayerData playerData) {
        player.sendMessage(ChatColor.GREEN + "Force refreshing challenges for " + playerData.getName() + "...");
        playerData.getChallengeTracker().refresh(true);
    }

    @Command(names = {"challenge list"}, permission = "op", async = true)
    public static void getChallenges(Player player, PlayerData playerData) {
        player.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Challenges for " + playerData.getName());
        player.sendMessage(ChatColor.GRAY + "Weekly challenges expire on " + WordUtils.capitalizeFully(ChallengeTracker.WEEKLY_RESET_DAY.name().toLowerCase()) + " at " + ChallengeTracker.RESET_TIME.format(ChallengeTracker.TIME_FORMATTER));
        ChallengeTracker tracker = playerData.getChallengeTracker();
        Multimap<Challenge.Duration, PlayerChallenge> challenges = tracker.getChallenges();

        for (Challenge.Duration duration : Challenge.Duration.values()) {
            player.sendMessage(ChatColor.YELLOW + duration.getDisplayName());

            if (challenges.containsKey(duration)) {
                for (PlayerChallenge playerChallenge : challenges.get(duration)) {
                    Challenge challenge = playerChallenge.getChallenge();
                    player.sendMessage(ChatColor.LIGHT_PURPLE + challenge.getName() +
                            (playerChallenge.isCompleted() ?
                                    ChatColor.GREEN + "\u2713" :
                                    ChatColor.YELLOW + " " + playerChallenge.getProgress() + ChatColor.GRAY + " (" + ChatColor.WHITE + playerChallenge.getValue() + "/" + challenge.getMaxValue() + ChatColor.GRAY + ")"
                            ) +
                            ChatColor.DARK_GRAY + " [" + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(playerChallenge.getChallenge().getDuration() == Challenge.Duration.WEEKLY ? tracker.getWeeklyExpiry() : tracker.getDailyExpiry()))
                    );
                }
            } else {
                player.sendMessage(ChatColor.RED + "No active challenges");
            }
        }
    }

}
