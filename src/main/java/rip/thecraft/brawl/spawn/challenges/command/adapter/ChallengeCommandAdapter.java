package rip.thecraft.brawl.spawn.challenges.command.adapter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.challenges.Challenge;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChallengeCommandAdapter implements ParameterType<Challenge> {

    @Override
    public Challenge transform(CommandSender sender, String source) {
        Challenge challenge = Brawl.getInstance().getChallengeHandler().getChallengeByName(source);;
        if (challenge == null) {
            sender.sendMessage(ChatColor.RED + "Challenge " + source + " not found.");
        }
        return challenge;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Challenge value : Brawl.getInstance().getChallengeHandler().getChallenges()) {
            if (StringUtils.startsWithIgnoreCase(value.getName().replace(" ", ""), source)) {
                completions.add(value.getName().replace(" ", ""));
            }
        }
        return completions;
    }
}
