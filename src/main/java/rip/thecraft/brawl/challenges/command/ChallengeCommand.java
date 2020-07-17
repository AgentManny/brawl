package rip.thecraft.brawl.challenges.command;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.PlayerChallenge;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.concurrent.TimeUnit;

public class ChallengeCommand {

    @Command(names = "challenge refresh", permission = "op", async = true)
    public static void refreshChallenges(Player player, PlayerData playerData) {
        player.sendMessage(ChatColor.GREEN + "Force refreshing challenges for " + playerData.getName() + "...");
    }

    @Command(names = {"challenge list"}, permission = "op", async = true)
    public static void getChallenges(Player player, PlayerData playerData) {
        player.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Active challenges for " + playerData.getName());
        Multimap<Challenge.Duration, PlayerChallenge> challenges = playerData.getAllChallenges();
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
                            ChatColor.DARK_GRAY + " [" + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(playerChallenge.getTimeLeft()))
                    );
                }
            } else {
                player.sendMessage(ChatColor.RED + "No active challenges");
            }
        }
    }

}
