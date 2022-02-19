package rip.thecraft.brawl.spawn.challenges.command;

import gg.manny.streamline.command.annotation.Command;
import gg.manny.streamline.command.annotation.Require;
import gg.manny.streamline.command.annotation.Sender;
import gg.manny.streamline.command.annotation.Text;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.challenges.ChallengeHandler;
import rip.thecraft.brawl.spawn.challenges.menu.create.ChallengeCreateMenu;

@Require("brawl.challenge.manage")
@RequiredArgsConstructor
public class ChallengeCreateCommand {

    private final ChallengeHandler challengeHandler;

    @Command(name = "create", desc = "Creates a challenge")
    public void execute(@Sender Player player, @Text String name) {
        if (challengeHandler.getChallengeByName(name) != null) {
            player.sendMessage(ChatColor.RED + "Error: Challenge " + ChatColor.YELLOW + name + ChatColor.RED + " already exists. " +
                    "Type " + ChatColor.YELLOW + "/challenge modify <challenge>" + ChatColor.RED + " instead.");
            return;
        }

        new ChallengeCreateMenu(challengeHandler, name).open(player);
    }
}
