package rip.thecraft.brawl.challenges.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.menu.ChallengeMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class ChallengeCommand {

    @Command(names = {"challenge info"})
    public static void info(CommandSender sender, Challenge challenge) {
        sender.sendMessage(challenge.getName() + " information");
        sender.sendMessage(challenge.getDescription());
    }

    @Command(names = {"challenge list"})
    public static void list(CommandSender sender) {
        sender.sendMessage(CC.GOLD + "Weekly challenges");
        Brawl.getInstance().getChallengeHandler().getWeeklyChallenges().forEach(challenge -> sender.sendMessage(CC.YELLOW + challenge.getName()));
        sender.sendMessage(CC.GOLD + "Daily challenges");
        Brawl.getInstance().getChallengeHandler().getDailyChallenges().forEach(challenge -> sender.sendMessage(CC.YELLOW + challenge.getName()));
    }

    @Command(names = {"challenge"})
    public static void execute(Player player) {
        new ChallengeMenu().openMenu(player);
    }

    @Command(names = {"challenge debug"})
    public static void debug(Player player, PlayerData playerData) {
        player.sendMessage("weekly");
        if (playerData.hasActiveWeeklyChallenge() || playerData.getWeeklyChallenge() != null) {
            player.sendMessage(playerData.getWeeklyChallenge().getName());
            player.sendMessage(playerData.getWeeklyChallenge().getDisplayName());
            player.sendMessage(String.valueOf(playerData.getWeeklyChallenge().getTimestamp()));
        }

        player.sendMessage("daily");
        if (playerData.hasActiveDailyChallenge() || playerData.getDailyChallenge() != null) {
            player.sendMessage(playerData.getDailyChallenge().getName());
            player.sendMessage(playerData.getDailyChallenge().getDisplayName());
            player.sendMessage(String.valueOf(playerData.getDailyChallenge().getTimestamp()));
        }


    }

}
