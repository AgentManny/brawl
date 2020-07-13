package rip.thecraft.brawl.challenges.command;

import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class ChallengeCommand {

    @Command(names = {"challenge info"})
    public static void info(CommandSender sender, Challenge challenge) {
        sender.sendMessage(challenge.getName() + " information");
        sender.sendMessage(challenge.getDescription());
    }

    @Command(names = {"challenge list"})
    public static void execute(CommandSender sender) {
        sender.sendMessage(CC.GOLD + "Weekly challenges");
        Brawl.getInstance().getChallengeHandler().getWeeklyChallenges().forEach(challenge -> sender.sendMessage(CC.YELLOW + challenge.getName()));
        sender.sendMessage(CC.GOLD + "Daily challenges");
        Brawl.getInstance().getChallengeHandler().getDailyChallenges().forEach(challenge -> sender.sendMessage(CC.YELLOW + challenge.getName()));
    }

}
