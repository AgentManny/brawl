package rip.thecraft.brawl.challenges.command.adapter;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.spartan.command.ParameterType;

import java.util.List;
import java.util.Set;

public class ChallengeCommandAdapter implements ParameterType<Challenge> {

    @Override
    public Challenge transform(CommandSender sender, String source) {
        Challenge challenge = Brawl.getInstance().getChallengeHandler().getByName(source);
        if (challenge == null) {
            sender.sendMessage(ChatColor.RED + "Challenge " + source + " not found.");
        }
        return challenge;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = Lists.newArrayList();
        Brawl.getInstance().getChallengeHandler().getDailyChallenges().forEach(challenge -> completions.add(challenge.getName()));
        Brawl.getInstance().getChallengeHandler().getWeeklyChallenges().forEach(challenge -> completions.add(challenge.getName()));
        return completions;
    }
}
